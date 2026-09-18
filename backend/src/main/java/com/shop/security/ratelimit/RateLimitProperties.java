package com.shop.security.ratelimit;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "app.rate-limit")
public class RateLimitProperties {

  /** Nombre de requêtes autorisées par fenêtre, par IP et par endpoint limité. */
  private int maxRequests = 5;

  /** Taille de la fenêtre glissante, en millisecondes. */
  private long windowMs = 60_000;
}
