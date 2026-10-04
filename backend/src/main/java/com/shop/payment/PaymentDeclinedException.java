package com.shop.payment;

/** Paiement refusé (carte invalide, expirée, refusée…) — message affichable au client. */
public class PaymentDeclinedException extends RuntimeException {

  public PaymentDeclinedException(String message) {
    super(message);
  }
}
