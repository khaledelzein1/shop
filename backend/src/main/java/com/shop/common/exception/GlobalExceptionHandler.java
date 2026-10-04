package com.shop.common.exception;

import com.shop.payment.PaymentDeclinedException;
import com.shop.payment.PaymentUnavailableException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Point central de gestion des erreurs : traduit chaque exception métier ou technique en un {@link
 * ApiError} JSON cohérent plutôt que de laisser fuiter une stacktrace ou un format d'erreur par
 * défaut de Spring.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidation(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    List<ApiError.FieldError> fieldErrors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> new ApiError.FieldError(fe.getField(), fe.getDefaultMessage()))
            .toList();
    return build(HttpStatus.BAD_REQUEST, "Validation error", request, fieldErrors);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiError> handleNotFound(
      ResourceNotFoundException ex, HttpServletRequest request) {
    return build(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
  }

  @ExceptionHandler(EmailAlreadyUsedException.class)
  public ResponseEntity<ApiError> handleConflict(
      EmailAlreadyUsedException ex, HttpServletRequest request) {
    return build(HttpStatus.CONFLICT, ex.getMessage(), request, null);
  }

  @ExceptionHandler(InvalidCurrentPasswordException.class)
  public ResponseEntity<ApiError> handleInvalidCurrentPassword(
      InvalidCurrentPasswordException ex, HttpServletRequest request) {
    return build(
        HttpStatus.BAD_REQUEST,
        ex.getMessage(),
        request,
        List.of(new ApiError.FieldError("currentPassword", ex.getMessage())));
  }

  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<ApiError> handleConflict(ConflictException ex, HttpServletRequest request) {
    return build(HttpStatus.CONFLICT, ex.getMessage(), request, null);
  }

  @ExceptionHandler(PaymentDeclinedException.class)
  public ResponseEntity<ApiError> handlePaymentDeclined(
      PaymentDeclinedException ex, HttpServletRequest request) {
    return build(HttpStatus.PAYMENT_REQUIRED, ex.getMessage(), request, null);
  }

  @ExceptionHandler(PaymentUnavailableException.class)
  public ResponseEntity<ApiError> handlePaymentUnavailable(
      PaymentUnavailableException ex, HttpServletRequest request) {
    return build(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), request, null);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ApiError> handleBadCredentials(
      BadCredentialsException ex, HttpServletRequest request) {
    return build(HttpStatus.UNAUTHORIZED, "Incorrect username/email or password", request, null);
  }

  @ExceptionHandler(DisabledException.class)
  public ResponseEntity<ApiError> handleDisabled(DisabledException ex, HttpServletRequest request) {
    return build(HttpStatus.UNAUTHORIZED, "This account is disabled", request, null);
  }

  /**
   * Filet de sécurité pour les autres AuthenticationException (locked, expired...) non gérées
   * explicitement.
   */
  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ApiError> handleAuthentication(
      AuthenticationException ex, HttpServletRequest request) {
    return build(HttpStatus.UNAUTHORIZED, "Authentication failed", request, null);
  }

  /**
   * Deux checkouts concurrents sur la même variante (verrou optimiste @Version côté {@code
   * ProductVariant}) — 409 plutôt qu'un 500 générique, avec un message qui invite à réessayer.
   */
  @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
  public ResponseEntity<ApiError> handleOptimisticLocking(
      ObjectOptimisticLockingFailureException ex, HttpServletRequest request) {
    return build(
        HttpStatus.CONFLICT,
        "This resource was modified in the meantime (e.g. stock changed by another order) — please try again",
        request,
        null);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiError> handleAccessDenied(
      AccessDeniedException ex, HttpServletRequest request) {
    return build(HttpStatus.FORBIDDEN, "Access denied", request, null);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest request) {
    return build(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request, null);
  }

  private ResponseEntity<ApiError> build(
      HttpStatus status,
      String message,
      HttpServletRequest request,
      List<ApiError.FieldError> fieldErrors) {
    ApiError error =
        new ApiError(
            Instant.now(),
            status.value(),
            status.getReasonPhrase(),
            message,
            request.getRequestURI(),
            fieldErrors);
    return ResponseEntity.status(status).body(error);
  }
}
