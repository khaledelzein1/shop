package com.shop.payment;

/** Comment le checkout encaisse la carte, selon qu'une clé Stripe est configurée ou non. */
public enum PaymentProvider {
  /** Page de paiement hébergée par Stripe (vrai paiement, ou mode test Stripe). */
  STRIPE,
  /** Formulaire de carte intégré, traité par {@link DemoCardProcessor} — aucun débit réel. */
  DEMO;

  public static PaymentProvider of(StripeProperties stripeProperties) {
    return stripeProperties.isConfigured() ? STRIPE : DEMO;
  }
}
