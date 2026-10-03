import { DatePipe, DecimalPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AdminOrderSummary, OrderStatus } from '../../../core/models/order.model';
import { ApiError } from '../../../core/models/page.model';
import { AdminOrderService } from '../../../core/services/admin-order.service';

const ALL_STATUSES: OrderStatus[] = ['PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED'];

@Component({
  selector: 'app-admin-orders',
  standalone: true,
  imports: [FormsModule, DecimalPipe, DatePipe],
  templateUrl: './orders.html',
})
export class AdminOrders implements OnInit {
  private readonly orderService = inject(AdminOrderService);

  protected readonly orders = signal<AdminOrderSummary[]>([]);
  protected readonly loading = signal(true);
  protected readonly statusFilter = signal<OrderStatus | ''>('');
  protected readonly statuses = ALL_STATUSES;

  ngOnInit(): void {
    this.load();
  }

  onFilterChange(): void {
    this.load();
  }

  changeStatus(orderId: number, status: OrderStatus): void {
    this.orderService.updateStatus(orderId, status).subscribe({
      next: () => this.load(),
      error: (err: HttpErrorResponse) => {
        const apiError = err.error as ApiError | undefined;
        alert(apiError?.message ?? 'Status transition refused');
        this.load();
      },
    });
  }

  private load(): void {
    this.loading.set(true);
    this.orderService.search(this.statusFilter(), 0, 50).subscribe((res) => {
      this.orders.set(res.content);
      this.loading.set(false);
    });
  }
}
