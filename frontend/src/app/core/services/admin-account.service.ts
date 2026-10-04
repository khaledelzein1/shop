import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthResponse, UpdateAccountPayload } from '../models/user.model';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class AdminAccountService {
  private readonly http = inject(HttpClient);
  private readonly auth = inject(AuthService);

  /** Changes the admin's own login credentials; the response carries fresh tokens for this session. */
  update(payload: UpdateAccountPayload): Observable<AuthResponse> {
    return this.http
      .put<AuthResponse>(`${environment.apiUrl}/admin/account`, payload)
      .pipe(tap((res) => this.auth.applySession(res)));
  }
}
