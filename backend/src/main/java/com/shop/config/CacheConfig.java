package com.shop.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cache in-memory (Caffeine) — un seul cache pour l'instant : la liste des catégories, lue à chaque
 * chargement du catalogue mais qui change rarement. TTL court plutôt qu'infini pour ne pas avoir à
 * gérer une invalidation parfaite partout (un admin qui modifie une catégorie verra le changement
 * se propager en quelques minutes, pas besoin de plus pour ce volume de données).
 */
@Configuration
@EnableCaching
public class CacheConfig {

  @Bean
  public CacheManager cacheManager() {
    CaffeineCacheManager manager = new CaffeineCacheManager("categories");
    manager.setCaffeine(
        Caffeine.newBuilder().expireAfterWrite(Duration.ofMinutes(10)).maximumSize(10));
    return manager;
  }
}
