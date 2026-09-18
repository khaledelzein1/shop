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

  private long expirationMs;
}
