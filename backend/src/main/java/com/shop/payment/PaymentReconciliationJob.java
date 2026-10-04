package com.shop.payment;

import com.shop.order.OrderRepository;
import com.shop.order.OrderService;
import com.shop.order.OrderStatus;
import java.time.Duration;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Filet de sécurité pour les paiements dont le client n'est jamais revenu sur le site (onglet fermé
 * après avoir payé, ou page Stripe abandonnée) : relit régulièrement chez Stripe les commandes
 * PENDING pour les confirmer si elles ont été payées, ou les annuler (et restituer le stock) une
 * fois la session expirée. En production, un webhook Stripe ferait la même chose en temps réel ; ce
 * job a l'avantage de marcher en local sans URL publique.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.stripe", name = "reconcile-enabled", matchIfMissing = true)
public class PaymentReconciliationJob {

  /** Laisse au client le temps de revenir lui-même de la page Stripe avant d'intervenir. */
  private static final Duration GRACE_PERIOD = Duration.ofMinutes(5);

  private final OrderRepository orderRepository;
  private final OrderService orderService;
  private final StripeProperties properties;

  @Scheduled(
      initialDelayString = "${app.stripe.reconcile-interval-ms:300000}",
      fixedDelayString = "${app.stripe.reconcile-interval-ms:300000}")
  public void reconcile() {
    if (!properties.isConfigured()) {
      return;
    }
    Instant cutoff = Instant.now().minus(GRACE_PERIOD);
    for (Long orderId : orderRepository.findIdsAwaitingPayment(OrderStatus.PENDING, cutoff)) {
      try {
        orderService.syncPayment(orderId);
      } catch (RuntimeException e) {
        // Une commande en échec (Stripe injoignable…) ne bloque pas les suivantes.
        log.warn("Payment reconciliation failed for order {}: {}", orderId, e.getMessage());
      }
    }
  }
}
