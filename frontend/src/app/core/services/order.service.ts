import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Order, OrderSummary } from '../models/order.model';
import { PageResponse } from '../models/page.model';

@Injectable({ providedIn: 'root' })
export class OrderService {
  constructor(private readonly http: HttpClient) {}

  checkout(addressId: number): Observable<Order> {
    return this.http.post<Order>(`${environment.apiUrl}/me/orders/checkout`, { addressId });
  }

  history(page: number, size: number): Observable<PageResponse<OrderSummary>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<OrderSummary>>(`${environment.apiUrl}/me/orders`, { params });
  }

  getById(id: number): Observable<Order> {
    return this.http.get<Order>(`${environment.apiUrl}/me/orders/${id}`);
  }
}
