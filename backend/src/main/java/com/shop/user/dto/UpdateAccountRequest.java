package com.shop.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Changement des identifiants de connexion de l'admin. {@code newPassword} est optionnel (null ou
 * vide = inchangé) ; le mot de passe actuel est toujours exigé.
 */
public record UpdateAccountRequest(
    @NotBlank String currentPassword,
    @NotBlank
        @Size(min = 3, max = 30, message = "Username must be 3 to 30 characters long")
        @Pattern(
            regexp = "^[A-Za-z0-9._-]+$",
            message = "Username may only contain letters, digits, '.', '_' and '-'")
        String username,
    @Size(min = 8, max = 100, message = "Password must be at least 8 characters long")
        String newPassword) {}
