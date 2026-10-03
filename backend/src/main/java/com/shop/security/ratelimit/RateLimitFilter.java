package com.shop.security.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.common.exception.ApiError;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Limite le nombre de tentatives sur les endpoints les plus exposés au brute-force (login,
 * register) — fenêtre fixe en mémoire, par IP.
 *
 * <p>Compromis assumé pour un projet monolithe mono-instance : pas de backend partagé
 * (Redis/Bucket4j-redis) donc ça ne protège qu'une seule instance et ne résiste pas à une attaque
 * distribuée sur beaucoup d'IP différentes — suffisant ici, une vraie mise à l'échelle passerait
 * par un rate limiter partagé.
 */
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class RateLimitFilter extends OncePerRequestFilter {

  private static final Set<String> LIMITED_PATHS = Set.of("/api/auth/login", "/api/auth/register");

  private final RateLimitProperties properties;
  private final ObjectMapper objectMapper;

  private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    if (!LIMITED_PATHS.contains(request.getRequestURI())) {
      filterChain.doFilter(request, response);
      return;
    }

    String key = clientIp(request) + ":" + request.getRequestURI();
    if (isRateLimited(key)) {
      writeTooManyRequests(response, request.getRequestURI());
      return;
    }

    filterChain.doFilter(request, response);
  }

  private boolean isRateLimited(String key) {
    long now = System.currentTimeMillis();
    Window window = windows.computeIfAbsent(key, k -> new Window(now));

    synchronized (window) {
      if (now - window.startMillis.get() > properties.getWindowMs()) {
        window.startMillis.set(now);
        window.count.set(0);
      }
      return window.count.incrementAndGet() > properties.getMaxRequests();
    }
  }

  private String clientIp(HttpServletRequest request) {
    String forwardedFor = request.getHeader("X-Forwarded-For");
    if (forwardedFor != null && !forwardedFor.isBlank()) {
      return forwardedFor.split(",")[0].trim();
    }
    return request.getRemoteAddr();
  }

  private void writeTooManyRequests(HttpServletResponse response, String path) throws IOException {
    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    ApiError error =
        new ApiError(
            Instant.now(),
            HttpStatus.TOO_MANY_REQUESTS.value(),
            HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase(),
            "Too many attempts, please try again shortly",
            path,
            null);
    response.getWriter().write(objectMapper.writeValueAsString(error));
  }

  private static final class Window {
    private final AtomicLong startMillis;
    private final AtomicInteger count = new AtomicInteger(0);

    private Window(long startMillis) {
      this.startMillis = new AtomicLong(startMillis);
    }
  }
}
