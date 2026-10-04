import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { ApiError } from '../../../core/models/page.model';
import { AdminAccountService } from '../../../core/services/admin-account.service';
import { AuthService } from '../../../core/services/auth.service';

/** New password is optional, but when filled it must be confirmed. */
function passwordsMatch(group: AbstractControl): ValidationErrors | null {
  const next = group.get('newPassword')?.value as string;
  const confirm = group.get('confirmPassword')?.value as string;
  return next && next !== confirm ? { passwordMismatch: true } : null;
}

@Component({
  selector: 'app-admin-account',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './account.html',
})
export class AdminAccount {
  private readonly fb = inject(FormBuilder);
  private readonly accountService = inject(AdminAccountService);
  protected readonly auth = inject(AuthService);

  protected readonly saving = signal(false);
  protected readonly successMessage = signal<string | null>(null);
  protected readonly errorMessage = signal<string | null>(null);

  protected readonly form = this.fb.nonNullable.group(
    {
      username: [
        this.auth.currentUser()?.username ?? '',
        [Validators.required, Validators.minLength(3), Validators.maxLength(30), Validators.pattern(/^[A-Za-z0-9._-]+$/)],
      ],
      newPassword: ['', [Validators.minLength(8)]],
      confirmPassword: [''],
      currentPassword: ['', [Validators.required]],
    },
    { validators: passwordsMatch },
  );

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const { username, newPassword, currentPassword } = this.form.getRawValue();
    this.saving.set(true);
    this.successMessage.set(null);
    this.errorMessage.set(null);

    this.accountService
      .update({ username: username.trim(), currentPassword, newPassword: newPassword || null })
      .subscribe({
        next: () => {
          this.saving.set(false);
          this.successMessage.set(
            newPassword
              ? 'Saved. Your password was changed and other devices were signed out.'
              : 'Saved. You can now log in with your new username.',
          );
          this.form.reset({ username: username.trim(), newPassword: '', confirmPassword: '', currentPassword: '' });
        },
        error: (err: HttpErrorResponse) => {
          this.saving.set(false);
          const apiError = err.error as ApiError | undefined;
          this.errorMessage.set(apiError?.fieldErrors?.[0]?.message ?? apiError?.message ?? 'An error occurred');
        },
      });
  }
}
