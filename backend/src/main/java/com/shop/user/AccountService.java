package com.shop.user;

import com.shop.auth.AuthService;
import com.shop.auth.dto.AuthResponse;
import com.shop.common.exception.ConflictException;
import com.shop.common.exception.InvalidCurrentPasswordException;
import com.shop.common.exception.ResourceNotFoundException;
import com.shop.security.jwt.RefreshTokenService;
import com.shop.user.dto.UpdateAccountRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Identifiants de connexion (nom d'utilisateur, mot de passe) choisis par l'admin lui-même. */
@Service
@RequiredArgsConstructor
public class AccountService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final RefreshTokenService refreshTokenService;
  private final AuthService authService;

  /**
   * Met à jour les identifiants après vérification du mot de passe actuel. Si le mot de passe
   * change, toutes les sessions existantes sont révoquées (autres appareils déconnectés) et un
   * nouveau couple de tokens est renvoyé pour garder la session courante ouverte.
   */
  @Transactional
  public AuthResponse update(Long userId, UpdateAccountRequest request) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

    if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
      throw new InvalidCurrentPasswordException();
    }

    String username = request.username().trim();
    if (userRepository.existsByUsernameIgnoreCaseAndIdNot(username, user.getId())) {
      throw new ConflictException("This username is already taken: " + username);
    }
    user.setUsername(username);

    boolean passwordChanged = request.newPassword() != null && !request.newPassword().isBlank();
    if (passwordChanged) {
      user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
      refreshTokenService.revokeAll(user);
    }

    return authService.issueTokens(user);
  }
}
