package com.shop.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

  /** Clé de signature HMAC — doit faire au moins 256 bits (32 caractères). */
  private String secret;

  /** Durée de vie de l'access token — courte puisqu'un refresh token existe désormais. */
  private long expirationMs;

  /** Durée de vie du refresh token (opaque, stocké hashé en base). */
  private long refreshExpirationMs;
}
