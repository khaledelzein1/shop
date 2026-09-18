import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

/** Un 401 signifie un token absent/expiré/invalide : on déconnecte proprement plutôt que de laisser l'UI dans un état incohérent. */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && authService.isAuthenticated()) {
        authService.logout();
      }
      return throwError(() => error);
    }),
  );
};
