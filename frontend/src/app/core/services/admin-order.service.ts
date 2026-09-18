import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AdminOrderSummary, Order, OrderStatus } from '../models/order.model';
import { PageResponse } from '../models/page.model';

@Injectable({ providedIn: 'root' })
export class AdminOrderService {
  constructor(private readonly http: HttpClient) {}

  search(status: OrderStatus | '', page: number, size: number): Observable<PageResponse<AdminOrderSummary>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (status) params = params.set('status', status);
    return this.http.get<PageResponse<AdminOrderSummary>>(`${environment.apiUrl}/admin/orders`, { params });
  }

  getById(id: number): Observable<Order> {
    return this.http.get<Order>(`${environment.apiUrl}/admin/orders/${id}`);
  }

  updateStatus(id: number, status: OrderStatus): Observable<Order> {
    return this.http.patch<Order>(`${environment.apiUrl}/admin/orders/${id}/status`, { status });
  }
}
