import { HttpErrorResponse, HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

const AUTH_ENDPOINTS = ['/auth/login', '/auth/register', '/auth/refresh'];

/**
 * Un 401 sur un appel authentifié signifie un access token expiré : on tente un refresh (rotation)
 * puis on rejoue la requête une seule fois. Si le refresh échoue aussi (refresh token expiré/révoqué),
 * on déconnecte proprement plutôt que de laisser l'UI dans un état incohérent.
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      const isAuthEndpoint = AUTH_ENDPOINTS.some((path) => req.url.includes(path));

      if (error.status === 401 && authService.isAuthenticated() && !isAuthEndpoint) {
        return authService.refresh().pipe(
          switchMap(() => next(withFreshToken(req, authService.getToken()))),
          catchError((refreshError) => {
            authService.logout();
            return throwError(() => refreshError);
          }),
        );
      }

      if (error.status === 401 && authService.isAuthenticated()) {
        authService.logout();
      }
      return throwError(() => error);
    }),
  );
};

function withFreshToken(req: HttpRequest<unknown>, token: string | null): HttpRequest<unknown> {
  return token ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req;
}
