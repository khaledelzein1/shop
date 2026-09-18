import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AdminUser } from '../../../core/models/user.model';
import { ApiError } from '../../../core/models/page.model';
import { AdminUserService } from '../../../core/services/admin-user.service';

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './users.html',
})
export class AdminUsers implements OnInit {
  private readonly userService = inject(AdminUserService);

  protected readonly users = signal<AdminUser[]>([]);
  protected readonly loading = signal(true);
  protected searchQuery = '';

  ngOnInit(): void {
    this.load();
  }

  search(): void {
    this.load();
  }

  toggleStatus(user: AdminUser): void {
    this.userService.updateStatus(user.id, !user.enabled).subscribe({
      next: () => this.load(),
      error: (err: HttpErrorResponse) => {
        const apiError = err.error as ApiError | undefined;
        alert(apiError?.message ?? "Action impossible");
      },
    });
  }

  private load(): void {
    this.loading.set(true);
    this.userService.search(this.searchQuery, 0, 50).subscribe((res) => {
      this.users.set(res.content);
      this.loading.set(false);
    });
  }
}
