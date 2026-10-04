import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { SalesReport } from '../models/sales.model';

@Injectable({ providedIn: 'root' })
export class AdminSalesService {
  private readonly http = inject(HttpClient);

  report(days: number): Observable<SalesReport> {
    const params = new HttpParams().set('days', days);
    return this.http.get<SalesReport>(`${environment.apiUrl}/admin/sales`, { params });
  }
}
