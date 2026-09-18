import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PageResponse } from '../models/page.model';
import { AdminUser } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class AdminUserService {
  private readonly http = inject(HttpClient);

  search(q: string, page: number, size: number): Observable<PageResponse<AdminUser>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (q) params = params.set('q', q);
    return this.http.get<PageResponse<AdminUser>>(`${environment.apiUrl}/admin/users`, { params });
  }

  updateStatus(id: number, enabled: boolean): Observable<AdminUser> {
    return this.http.patch<AdminUser>(`${environment.apiUrl}/admin/users/${id}/status`, {
      enabled,
    });
  }
}
