package com.shop.order.dto;

import com.shop.payment.CardDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * @param card carte saisie dans le formulaire intégré — obligatoire en mode de paiement DEMO,
 *     ignorée en mode STRIPE (la carte est alors saisie sur la page de Stripe).
 */
public record CheckoutRequest(@NotNull Long addressId, @Valid CardDetails card) {

  public CheckoutRequest(Long addressId) {
    this(addressId, null);
  }
}
