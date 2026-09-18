import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { OrderSummary } from '../../../core/models/order.model';
import { OrderService } from '../../../core/services/order.service';

@Component({
  selector: 'app-orders-list',
  standalone: true,
  imports: [RouterLink, DecimalPipe, DatePipe],
  templateUrl: './orders-list.html',
  styleUrl: './orders-list.scss',
})
export class OrdersList implements OnInit {
  private readonly orderService = inject(OrderService);

  protected readonly orders = signal<OrderSummary[]>([]);
  protected readonly loading = signal(true);

  ngOnInit(): void {
    this.orderService.history(0, 20).subscribe((res) => {
      this.orders.set(res.content);
      this.loading.set(false);
    });
  }
}
