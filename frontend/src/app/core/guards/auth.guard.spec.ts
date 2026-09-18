import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, provideRouter, Router, RouterStateSnapshot } from '@angular/router';
import { beforeEach, describe, expect, it } from 'vitest';
import { adminGuard, authGuard } from './auth.guard';

const configureTestBed = () => {
  TestBed.configureTestingModule({
    providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
  });
};

const storeUser = (roles: string[]) => {
  localStorage.setItem('shop_token', 'fake-token');
  localStorage.setItem(
    'shop_user',
    JSON.stringify({ id: 1, email: 'a@b.com', firstName: 'A', lastName: 'B', roles }),
  );
};

describe('authGuard', () => {
  beforeEach(() => localStorage.clear());

  it('redirects to /login when not authenticated', () => {
    configureTestBed();
    const result = TestBed.runInInjectionContext(() =>
      authGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot),
    );
    const router = TestBed.inject(Router);
    expect(result).toEqual(router.parseUrl('/login'));
  });

  it('allows access when authenticated', () => {
    storeUser(['ROLE_USER']);
    configureTestBed();

    const result = TestBed.runInInjectionContext(() =>
      authGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot),
    );
    expect(result).toBe(true);
  });
});

describe('adminGuard', () => {
  beforeEach(() => localStorage.clear());

  it('redirects non-admin users home', () => {
    storeUser(['ROLE_USER']);
    configureTestBed();

    const result = TestBed.runInInjectionContext(() =>
      adminGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot),
    );
    const router = TestBed.inject(Router);
    expect(result).toEqual(router.parseUrl('/'));
  });

  it('allows ROLE_ADMIN users', () => {
    storeUser(['ROLE_ADMIN']);
    configureTestBed();

    const result = TestBed.runInInjectionContext(() =>
      adminGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot),
    );
    expect(result).toBe(true);
  });
});
