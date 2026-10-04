import { OrderStatus } from './order.model';

export interface DailySales {
  /** ISO date (yyyy-MM-dd). */
  date: string;
  revenue: number;
  orders: number;
}

export interface TopProduct {
  name: string;
  quantity: number;
  revenue: number;
}

export interface RecentSale {
  id: number;
  createdAt: string;
  customerEmail: string;
  total: number;
  status: OrderStatus;
}

/** Paid orders only (CONFIRMED, SHIPPED, DELIVERED) over the last `days` days. */
export interface SalesReport {
  days: number;
  revenue: number;
  orderCount: number;
  averageOrderValue: number;
  itemsSold: number;
  previousRevenue: number;
  previousOrderCount: number;
  daily: DailySales[];
  topProducts: TopProduct[];
  recentSales: RecentSale[];
}
