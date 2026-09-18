import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Order } from '../../../core/models/order.model';
import { OrderService } from '../../../core/services/order.service';

@Component({
  selector: 'app-order-detail',
  standalone: true,
  imports: [RouterLink, DecimalPipe, DatePipe],
  templateUrl: './order-detail.html',
  styleUrl: './order-detail.scss',
})
export class OrderDetail implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly orderService = inject(OrderService);

  protected readonly order = signal<Order | null>(null);
  protected readonly loading = signal(true);

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.orderService.getById(id).subscribe((order) => {
      this.order.set(order);
      this.loading.set(false);
    });
  }
}
