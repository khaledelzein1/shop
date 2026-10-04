import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DailySales, SalesReport } from '../../../core/models/sales.model';
import { AdminSalesService } from '../../../core/services/admin-sales.service';

/** Rounds up to a "nice" axis maximum: 1, 2 or 5 × 10ⁿ. */
function niceMax(value: number): number {
  if (value <= 0) {
    return 100;
  }
  const magnitude = 10 ** Math.floor(Math.log10(value));
  const step = [1, 2, 5, 10].find((s) => s * magnitude >= value) ?? 10;
  return step * magnitude;
}

@Component({
  selector: 'app-admin-sales',
  standalone: true,
  imports: [DecimalPipe, DatePipe, RouterLink],
  templateUrl: './sales.html',
  styleUrl: './sales.scss',
})
export class AdminSales implements OnInit {
  private readonly salesService = inject(AdminSalesService);

  protected readonly periods = [7, 30, 90, 365];
  protected readonly days = signal(30);
  protected readonly report = signal<SalesReport | null>(null);
  protected readonly loading = signal(true);
  protected readonly hovered = signal<number | null>(null);

  /** Y-axis maximum and its gridlines (0, ½, max). */
  protected readonly axisMax = computed(() =>
    niceMax(Math.max(0, ...(this.report()?.daily.map((d) => d.revenue) ?? [0]))),
  );
  protected readonly gridlines = computed(() => {
    const max = this.axisMax();
    return [max, max / 2, 0];
  });

  /** % change vs the previous period of the same length; null when there's nothing to compare. */
  protected readonly revenueChange = computed(() => {
    const r = this.report();
    if (!r || r.previousRevenue === 0) {
      return null;
    }
    return ((r.revenue - r.previousRevenue) / r.previousRevenue) * 100;
  });

  protected readonly topMax = computed(() =>
    Math.max(1, ...(this.report()?.topProducts.map((p) => p.quantity) ?? [1])),
  );

  ngOnInit(): void {
    this.load();
  }

  selectPeriod(days: number): void {
    if (days !== this.days()) {
      this.days.set(days);
      this.load();
    }
  }

  barHeight(day: DailySales): number {
    return (day.revenue / this.axisMax()) * 100;
  }

  /** Label every Nth day on the x-axis so labels never collide. */
  showTick(index: number, total: number): boolean {
    const every = total <= 10 ? 1 : Math.ceil(total / 8);
    return index % every === 0 || index === total - 1;
  }

  private load(): void {
    this.loading.set(true);
    this.hovered.set(null);
    this.salesService.report(this.days()).subscribe({
      next: (report) => {
        this.report.set(report);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }
}
