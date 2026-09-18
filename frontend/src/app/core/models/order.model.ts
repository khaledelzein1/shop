export type OrderStatus = 'PENDING' | 'CONFIRMED' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';

export interface OrderItem {
  id: number;
  productName: string;
  sku: string;
  quantity: number;
  unitPrice: number;
  lineTotal: number;
}

export interface ShippingAddress {
  label: string | null;
  street: string;
  city: string;
  zipCode: string;
  country: string;
}

export interface Order {
  id: number;
  status: OrderStatus;
  totalAmount: number;
  createdAt: string;
  items: OrderItem[];
  shippingAddress: ShippingAddress;
}

export interface OrderSummary {
  id: number;
  status: OrderStatus;
  totalAmount: number;
  createdAt: string;
  itemCount: number;
}
