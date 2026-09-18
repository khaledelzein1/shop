import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { environment } from '../../../environments/environment';
import { AuthService } from './auth.service';

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

  it('login stores the token and user, marking the session as authenticated', () => {
    service.login({ email: 'user@example.com', password: 'password123' }).subscribe();

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
    expect(req.request.method).toBe('POST');
    req.flush({
      accessToken: 'fake-token',
      tokenType: 'Bearer',
      expiresInSeconds: 86400,
      user: { id: 1, email: 'user@example.com', firstName: 'A', lastName: 'B', roles: ['ROLE_USER'] },
    });

    expect(service.isAuthenticated()).toBe(true);
    expect(service.getToken()).toBe('fake-token');
    expect(service.currentUser()?.email).toBe('user@example.com');
    expect(service.isAdmin()).toBe(false);
  });

  it('isAdmin is true only when the user has ROLE_ADMIN', () => {
    service.login({ email: 'admin@example.com', password: 'password123' }).subscribe();
    httpMock.expectOne(`${environment.apiUrl}/auth/login`).flush({
      accessToken: 'admin-token',
      tokenType: 'Bearer',
      expiresInSeconds: 86400,
      user: { id: 1, email: 'admin@example.com', firstName: 'Admin', lastName: 'Shop', roles: ['ROLE_ADMIN'] },
    });

    expect(service.isAdmin()).toBe(true);
  });

  it('logout clears the token and user', () => {
    service.login({ email: 'user@example.com', password: 'password123' }).subscribe();
    httpMock.expectOne(`${environment.apiUrl}/auth/login`).flush({
      accessToken: 'fake-token',
      tokenType: 'Bearer',
      expiresInSeconds: 86400,
      user: { id: 1, email: 'user@example.com', firstName: 'A', lastName: 'B', roles: ['ROLE_USER'] },
    });

    service.logout();

    expect(service.isAuthenticated()).toBe(false);
    expect(service.getToken()).toBeNull();
  });
});
