import { DecimalPipe } from '@angular/common';
import { Component, OnDestroy, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Order } from '../../core/models/order.model';
import { CartService } from '../../core/services/cart.service';
import { OrderService } from '../../core/services/order.service';

/** Nombre de vérifications si Stripe n'a pas encore fini de traiter le paiement (3-D Secure…). */
const MAX_CHECKS = 5;
const CHECK_INTERVAL_MS = 2000;

/**
 * Page de retour de Stripe Checkout (`/checkout/success` ou `/checkout/cancel`). Le paramètre
 * `order_id` de l'URL n'est qu'un identifiant : c'est le backend qui vérifie chez Stripe si la
 * commande a vraiment été payée.
 */
@Component({
  selector: 'app-payment-result',
  standalone: true,
  imports: [RouterLink, DecimalPipe],
  templateUrl: './payment-result.html',
  styleUrl: './payment-result.scss',
})
export class PaymentResult implements OnInit, OnDestroy {
  private readonly route = inject(ActivatedRoute);
  private readonly orderService = inject(OrderService);
  private readonly cartService = inject(CartService);

  protected readonly order = signal<Order | null>(null);
  protected readonly error = signal<string | null>(null);
  private checks = 0;
  private retryTimer: ReturnType<typeof setTimeout> | null = null;

  ngOnInit(): void {
    const orderId = Number(this.route.snapshot.queryParamMap.get('order_id'));
    if (!orderId) {
      this.error.set('This payment link is invalid.');
      return;
    }
    if (this.route.snapshot.data['outcome'] === 'cancel') {
      this.orderService.cancelPayment(orderId).subscribe({
        next: (order) => this.order.set(order),
        error: () => this.error.set('We could not cancel this payment. Please check your orders.'),
      });
    } else {
      this.checkPayment(orderId);
    }
  }

  ngOnDestroy(): void {
    if (this.retryTimer) {
      clearTimeout(this.retryTimer);
    }
  }

  private checkPayment(orderId: number): void {
    this.checks++;
    this.orderService.confirmPayment(orderId).subscribe({
      next: (order) => {
        this.order.set(order);
        if (order.status === 'CONFIRMED') {
          this.cartService.refresh().subscribe();
        } else if (order.status === 'PENDING' && this.checks < MAX_CHECKS) {
          this.retryTimer = setTimeout(() => this.checkPayment(orderId), CHECK_INTERVAL_MS);
        }
      },
      error: () => this.error.set('We could not verify your payment. Please check your orders.'),
    });
  }

  /** Vrai tant qu'on attend encore une réponse définitive de Stripe. */
  protected stillChecking(order: Order): boolean {
    return order.status === 'PENDING' && this.checks < MAX_CHECKS;
  }
}
