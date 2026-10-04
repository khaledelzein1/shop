package com.shop.order.dto;

/**
 * Résultat du checkout : la commande créée (PENDING tant que le paiement n'est pas confirmé) et
 * l'URL de la page de paiement Stripe vers laquelle rediriger le client.
 */
public record CheckoutResponse(OrderResponse order, String paymentUrl) {}
