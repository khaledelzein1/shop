package com.shop.payment;

import com.shop.order.Order;

/**
 * Passerelle de paiement par carte. Isolée derrière cette interface pour que le reste du code (et
 * les tests) ne dépende pas directement de Stripe.
 */
public interface PaymentGateway {

  /**
   * Ouvre une page de paiement hébergée pour la commande (déjà persistée, donc avec un id) et
   * renvoie l'id de la session et l'URL vers laquelle rediriger le client.
   */
  PaymentSession createSession(Order order, String customerEmail);

  /** Statut du paiement relu chez le prestataire — seule source de vérité. */
  PaymentStatus getStatus(String sessionId);

  /** Ferme une session encore ouverte pour qu'elle ne puisse plus être payée. */
  void expireSession(String sessionId);

  record PaymentSession(String id, String url) {}

  enum PaymentStatus {
    /** Le client a payé. */
    PAID,
    /** Session encore ouverte : le client peut encore payer. */
    OPEN,
    /** Session expirée ou fermée sans paiement. */
    EXPIRED
  }
}
