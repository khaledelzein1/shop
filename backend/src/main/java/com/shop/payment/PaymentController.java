package com.shop.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me/payment")
@RequiredArgsConstructor
@Tag(name = "Paiement", description = "Mode de paiement par carte actif")
public class PaymentController {

  private final StripeProperties stripeProperties;

  @GetMapping("/config")
  @Operation(
      summary = "Mode de paiement actif",
      description =
          "STRIPE : redirection vers la page de paiement Stripe. DEMO (aucune clé Stripe) : "
              + "formulaire de carte intégré au checkout, aucun débit réel.")
  public PaymentConfigResponse config() {
    return new PaymentConfigResponse(PaymentProvider.of(stripeProperties));
  }

  public record PaymentConfigResponse(PaymentProvider provider) {}
}
