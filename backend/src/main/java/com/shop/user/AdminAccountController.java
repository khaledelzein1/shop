package com.shop.user;

import com.shop.auth.dto.AuthResponse;
import com.shop.security.config.UserPrincipal;
import com.shop.user.dto.UpdateAccountRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/account")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Admin - Compte", description = "Identifiants de connexion de l'admin connecté (ADMIN)")
public class AdminAccountController {

  private final AccountService accountService;

  @PutMapping
  @Operation(
      summary = "Changer son nom d'utilisateur et/ou son mot de passe",
      description =
          "Exige le mot de passe actuel (400 sinon). Un nouveau mot de passe déconnecte les autres "
              + "sessions ; la réponse contient de nouveaux tokens pour la session courante.")
  public AuthResponse update(
      @AuthenticationPrincipal UserPrincipal principal,
      @Valid @RequestBody UpdateAccountRequest request) {
    return accountService.update(principal.getId(), request);
  }
}
