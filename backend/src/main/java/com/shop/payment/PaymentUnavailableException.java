package com.shop.payment;

/** Le paiement par carte n'est pas configuré, ou Stripe n'a pas pu être joint. */
public class PaymentUnavailableException extends RuntimeException {

  public PaymentUnavailableException(String message) {
    super(message);
  }

  public PaymentUnavailableException(String message, Throwable cause) {
    super(message, cause);
  }
}
