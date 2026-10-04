package com.shop.auth.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;

/**
 * {@code login} : email ou nom d'utilisateur. L'alias {@code email} garde compatibles les clients
 * qui envoient encore l'ancien format.
 */
public record LoginRequest(@NotBlank @JsonAlias("email") String login, @NotBlank String password) {}
