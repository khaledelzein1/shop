package com.shop.payment;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Carte saisie dans le formulaire de paiement intégré (mode démo). N'est jamais persistée ni
 * journalisée : seuls la marque et les 4 derniers chiffres sont gardés sur la commande.
 */
public record CardDetails(
    @NotBlank @Size(max = 23) String number,
    @NotNull @Min(1) @Max(12) Integer expMonth,
    @NotNull @Min(0) @Max(9999) Integer expYear,
    @NotBlank @Size(max = 4) String cvc,
    @NotBlank @Size(max = 100) String holderName) {

  /** Masqué pour qu'un log ou un message d'erreur n'affiche jamais le numéro ni le CVC. */
  @Override
  public String toString() {
    String digits = number == null ? "" : number.replaceAll("\\D", "");
    String last4 = digits.length() >= 4 ? digits.substring(digits.length() - 4) : "????";
    return "CardDetails[number=**** " + last4 + ", cvc=***]";
  }
}
