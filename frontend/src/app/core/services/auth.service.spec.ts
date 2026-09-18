import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { environment } from '../../../environments/environment';
import { AuthService } from './auth.service';

const LOGIN_RESPONSE = {
  accessToken: 'fake-token',
  refreshToken: 'fake-refresh-token',
  tokenType: 'Bearer',
  expiresInSeconds: 900,
  user: { id: 1, email: 'user@example.com', firstName: 'A', lastName: 'B', roles: ['ROLE_USER'] },
};

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('starts unauthenticated when nothing is stored', () => {
    expect(service.isAuthenticated()).toBe(false);
    expect(service.currentUser()).toBeNull();
  });

  it('login stores access + refresh tokens and user, marking the session as authenticated', () => {
    service.login({ email: 'user@example.com', password: 'password123' }).subscribe();

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
    expect(req.request.method).toBe('POST');
    req.flush(LOGIN_RESPONSE);

    expect(service.isAuthenticated()).toBe(true);
    expect(service.getToken()).toBe('fake-token');
    expect(service.getRefreshToken()).toBe('fake-refresh-token');
    expect(service.currentUser()?.email).toBe('user@example.com');
    expect(service.isAdmin()).toBe(false);
  });

  it('isAdmin is true only when the user has ROLE_ADMIN', () => {
    service.login({ email: 'admin@example.com', password: 'password123' }).subscribe();
    httpMock.expectOne(`${environment.apiUrl}/auth/login`).flush({
      ...LOGIN_RESPONSE,
      accessToken: 'admin-token',
      user: { id: 1, email: 'admin@example.com', firstName: 'Admin', lastName: 'Shop', roles: ['ROLE_ADMIN'] },
    });

    expect(service.isAdmin()).toBe(true);
  });

  it('refresh rotates the stored tokens', () => {
    service.login({ email: 'user@example.com', password: 'password123' }).subscribe();
    httpMock.expectOne(`${environment.apiUrl}/auth/login`).flush(LOGIN_RESPONSE);

    service.refresh().subscribe();
    const refreshReq = httpMock.expectOne(`${environment.apiUrl}/auth/refresh`);
    expect(refreshReq.request.body).toEqual({ refreshToken: 'fake-refresh-token' });
    refreshReq.flush({ ...LOGIN_RESPONSE, accessToken: 'new-token', refreshToken: 'new-refresh-token' });

    expect(service.getToken()).toBe('new-token');
    expect(service.getRefreshToken()).toBe('new-refresh-token');
  });

  it('logout revokes the refresh token server-side and clears local state', () => {
    service.login({ email: 'user@example.com', password: 'password123' }).subscribe();
    httpMock.expectOne(`${environment.apiUrl}/auth/login`).flush(LOGIN_RESPONSE);

    service.logout();

    const logoutReq = httpMock.expectOne(`${environment.apiUrl}/auth/logout`);
    expect(logoutReq.request.body).toEqual({ refreshToken: 'fake-refresh-token' });
    logoutReq.flush(null);

    expect(service.isAuthenticated()).toBe(false);
    expect(service.getToken()).toBeNull();
    expect(service.getRefreshToken()).toBeNull();
  });
});
