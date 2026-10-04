import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CardDetails, CheckoutResponse, Order, OrderSummary, PaymentProvider } from '../models/order.model';
import { PageResponse } from '../models/page.model';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private readonly http = inject(HttpClient);

  /** `card` n'est envoyée qu'en mode DEMO ; en mode STRIPE la carte est saisie sur la page de Stripe. */
  checkout(addressId: number, card?: CardDetails): Observable<CheckoutResponse> {
    return this.http.post<CheckoutResponse>(`${environment.apiUrl}/me/orders/checkout`, { addressId, card });
  }

  paymentProvider(): Observable<{ provider: PaymentProvider }> {
    return this.http.get<{ provider: PaymentProvider }>(`${environment.apiUrl}/me/payment/config`);
  }

  /** Au retour de Stripe : le backend revérifie le paiement et confirme la commande si elle est payée. */
  confirmPayment(orderId: number): Observable<Order> {
    return this.http.post<Order>(`${environment.apiUrl}/me/orders/${orderId}/payment/confirm`, {});
  }

  /** Le client a quitté la page Stripe sans payer : la commande est annulée, le panier conservé. */
  cancelPayment(orderId: number): Observable<Order> {
    return this.http.post<Order>(`${environment.apiUrl}/me/orders/${orderId}/payment/cancel`, {});
  }

  history(page: number, size: number): Observable<PageResponse<OrderSummary>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<OrderSummary>>(`${environment.apiUrl}/me/orders`, { params });
  }

  getById(id: number): Observable<Order> {
    return this.http.get<Order>(`${environment.apiUrl}/me/orders/${id}`);
  }
}
