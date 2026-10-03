package com.shop.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.common.exception.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * Sans entry point explicite, Spring Security répond 403 (via l'{@code AccessDeniedHandler} par
 * défaut) aux requêtes non authentifiées faute de {@code httpBasic()}/{@code formLogin()} — ce qui
 * casse la convention REST standard (401 = pas authentifié / token absent-invalide-expiré, 403 =
 * authentifié mais droits insuffisants) et empêche le frontend de distinguer "il faut rafraîchir le
 * token" de "l'utilisateur n'a pas le rôle requis".
 */
@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    ApiError error =
        new ApiError(
            Instant.now(),
            HttpStatus.UNAUTHORIZED.value(),
            HttpStatus.UNAUTHORIZED.getReasonPhrase(),
            "Authentication required or invalid/expired token",
            request.getRequestURI(),
            null);
    response.getWriter().write(objectMapper.writeValueAsString(error));
  }
}
