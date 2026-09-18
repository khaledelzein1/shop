import { HttpClient } from '@angular/common/http';
import { Injectable, computed, signal, inject } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, catchError, finalize, of, shareReplay, tap, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthResponse, LoginPayload, RegisterPayload, User } from '../models/user.model';

const TOKEN_KEY = 'shop_token';
const REFRESH_TOKEN_KEY = 'shop_refresh_token';
const USER_KEY = 'shop_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  private readonly currentUserSignal = signal<User | null>(this.readStoredUser());

  readonly currentUser = this.currentUserSignal.asReadonly();
  readonly isAuthenticated = computed(() => this.currentUserSignal() !== null);
  readonly isAdmin = computed(
    () => this.currentUserSignal()?.roles.includes('ROLE_ADMIN') ?? false,
  );

  /** Déduplique les refresh concurrents (plusieurs requêtes en 401 en même temps ne déclenchent qu'un seul appel réseau). */
  private refreshInFlight: Observable<AuthResponse> | null = null;

  register(payload: RegisterPayload): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${environment.apiUrl}/auth/register`, payload)
      .pipe(tap((res) => this.handleAuthSuccess(res)));
  }

  login(payload: LoginPayload): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${environment.apiUrl}/auth/login`, payload)
      .pipe(tap((res) => this.handleAuthSuccess(res)));
  }

  /** Consomme le refresh token stocké (rotation) et remplace access+refresh token. */
  refresh(): Observable<AuthResponse> {
    const refreshToken = this.getRefreshToken();
    if (!refreshToken) {
      return throwError(() => new Error('Aucun refresh token disponible'));
    }

    if (!this.refreshInFlight) {
      this.refreshInFlight = this.http
        .post<AuthResponse>(`${environment.apiUrl}/auth/refresh`, { refreshToken })
        .pipe(
          tap((res) => this.handleAuthSuccess(res)),
          finalize(() => (this.refreshInFlight = null)),
          shareReplay(1),
        );
    }
    return this.refreshInFlight;
  }

  logout(): void {
    const refreshToken = this.getRefreshToken();
    if (refreshToken) {
      // best-effort : la session locale est nettoyée même si l'appel réseau échoue (offline...).
      this.http
        .post(`${environment.apiUrl}/auth/logout`, { refreshToken })
        .pipe(catchError(() => of(null)))
        .subscribe();
    }

    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this.currentUserSignal.set(null);
    this.router.navigate(['/']);
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  getRefreshToken(): string | null {
    return localStorage.getItem(REFRESH_TOKEN_KEY);
  }

  private handleAuthSuccess(res: AuthResponse): void {
    localStorage.setItem(TOKEN_KEY, res.accessToken);
    localStorage.setItem(REFRESH_TOKEN_KEY, res.refreshToken);
    localStorage.setItem(USER_KEY, JSON.stringify(res.user));
    this.currentUserSignal.set(res.user);
  }

  private readStoredUser(): User | null {
    const raw = localStorage.getItem(USER_KEY);
    if (!raw) {
      return null;
    }
    try {
      return JSON.parse(raw) as User;
    } catch {
      return null;
    }
  }
}
