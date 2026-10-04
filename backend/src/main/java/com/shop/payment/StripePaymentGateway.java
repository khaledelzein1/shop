package com.shop.payment;

import com.shop.catalog.ProductVariant;
import com.shop.order.Order;
import com.shop.order.OrderItem;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.LineItem;
import com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData;
import com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData.ProductData;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Paiement via Stripe Checkout : le client saisit sa carte sur la page hébergée par Stripe (3-D
 * Secure, Apple/Google Pay…), l'appli ne voit donc jamais de numéro de carte.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StripePaymentGateway implements PaymentGateway {

  private final StripeProperties properties;

  private volatile StripeClient client;

  @Override
  public PaymentSession createSession(Order order, String customerEmail) {
    String frontendUrl = properties.getFrontendUrl();
    SessionCreateParams.Builder params =
        SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setClientReferenceId(order.getId().toString())
            .putMetadata("orderId", order.getId().toString())
            .setCustomerEmail(customerEmail)
            // Le retour sur success_url ne prouve rien : le paiement est revérifié chez Stripe
            // à partir de l'id de session stocké sur la commande.
            .setSuccessUrl(frontendUrl + "/checkout/success?order_id=" + order.getId())
            .setCancelUrl(frontendUrl + "/checkout/cancel?order_id=" + order.getId())
            .setExpiresAt(
                Instant.now()
                    .plus(Duration.ofMinutes(properties.getSessionTtlMinutes()))
                    .getEpochSecond());

    for (OrderItem item : order.getItems()) {
      params.addLineItem(
          LineItem.builder()
              .setQuantity((long) item.getQuantity())
              .setPriceData(
                  PriceData.builder()
                      .setCurrency(properties.getCurrency())
                      // Montant en centimes, comme l'exige Stripe.
                      .setUnitAmount(item.getUnitPrice().movePointRight(2).longValueExact())
                      .setProductData(productData(item))
                      .build())
              .build());
    }

    try {
      Session session = client().v1().checkout().sessions().create(params.build());
      return new PaymentSession(session.getId(), session.getUrl());
    } catch (StripeException e) {
      log.error("Stripe session creation failed for order {}", order.getId(), e);
      throw new PaymentUnavailableException(
          "Card payment is temporarily unavailable — please try again", e);
    }
  }

  @Override
  public PaymentStatus getStatus(String sessionId) {
    try {
      Session session = client().v1().checkout().sessions().retrieve(sessionId);
      if ("paid".equals(session.getPaymentStatus())) {
        return PaymentStatus.PAID;
      }
      return "open".equals(session.getStatus()) ? PaymentStatus.OPEN : PaymentStatus.EXPIRED;
    } catch (StripeException e) {
      throw new PaymentUnavailableException("Could not check the payment status with Stripe", e);
    }
  }

  @Override
  public void expireSession(String sessionId) {
    try {
      client().v1().checkout().sessions().expire(sessionId);
    } catch (StripeException e) {
      // Déjà expirée/payée : pas grave, le statut sera relu juste après.
      log.warn("Could not expire Stripe session {}: {}", sessionId, e.getMessage());
    }
  }

  /** Nom affiché sur la page Stripe : le modèle (ex. "FLORAL PRINT T-SHIRT") + couleur/taille. */
  private static ProductData productData(OrderItem item) {
    ProductVariant variant = item.getVariant();
    Map<String, String> attributes = variant != null ? variant.getAttributes() : Map.of();
    String name = attributes.getOrDefault("model", item.getProductName());
    String details =
        Stream.of(attributes.get("color"), attributes.get("size"))
            .filter(v -> v != null && !v.isBlank())
            .collect(Collectors.joining(" / "));
    ProductData.Builder data = ProductData.builder().setName(name);
    if (!details.isEmpty()) {
      data.setDescription(details);
    }
    return data.build();
  }

  private StripeClient client() {
    if (!properties.isConfigured()) {
      throw new PaymentUnavailableException(
          "Card payment is not configured on this server (missing STRIPE_SECRET_KEY)");
    }
    if (client == null) {
      client = new StripeClient(properties.getSecretKey());
    }
    return client;
  }
}
