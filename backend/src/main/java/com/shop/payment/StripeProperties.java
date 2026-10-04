package com.shop.payment;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "app.stripe")
public class StripeProperties {

  /**
   * Clé secrète Stripe (sk_test_... en dev). Vide = paiement par carte désactivé : le checkout
   * répond 503 au lieu de créer une commande impayable.
   */
  private String secretKey;

  /** Devise des paiements (code ISO en minuscules). */
  private String currency = "eur";

  /** URL du frontend, vers laquelle Stripe renvoie le client après paiement ou annulation. */
  private String frontendUrl;

  /** Durée de validité d'une session de paiement — Stripe impose au moins 30 minutes. */
  private long sessionTtlMinutes = 30;

  public boolean isConfigured() {
    return secretKey != null && !secretKey.isBlank();
  }
}
