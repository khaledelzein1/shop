package com.shop.auth;

import com.shop.auth.dto.AuthResponse;
import com.shop.auth.dto.LoginRequest;
import com.shop.auth.dto.LogoutRequest;
import com.shop.auth.dto.RefreshRequest;
import com.shop.auth.dto.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Inscription, connexion et rotation de JWT")
public class AuthController {

  private final AuthService authService;

  @PostMapping("/register")
  @Operation(
      summary = "Créer un compte",
      description =
          "Crée un compte ROLE_USER et connecte immédiatement (retourne un access token + un refresh token).")
  public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
  }

  @PostMapping("/login")
  @Operation(
      summary = "Se connecter",
      description =
          "Retourne un access token (à utiliser en header Authorization: Bearer <token>, courte durée de vie) et un refresh token.")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
    return ResponseEntity.ok(authService.login(request));
  }

  @PostMapping("/refresh")
  @Operation(
      summary = "Renouveler l'access token",
      description =
          "Consomme le refresh token fourni (rotation) et renvoie un nouveau couple access/refresh token. 401 si invalide, expiré ou déjà utilisé.")
  public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
    return ResponseEntity.ok(authService.refresh(request.refreshToken()));
  }

  @PostMapping("/logout")
  @Operation(
      summary = "Se déconnecter",
      description =
          "Révoque le refresh token côté serveur — l'access token déjà émis reste valable jusqu'à son expiration (courte).")
  public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request) {
    authService.logout(request.refreshToken());
    return ResponseEntity.noContent().build();
  }
}
