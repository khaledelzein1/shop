export interface CartItem {
  id: number;
  variantId: number;
  productName: string;
  productSlug: string;
  sku: string;
  attributes: Record<string, string>;
  unitPrice: number;
  quantity: number;
  lineTotal: number;
}

export interface Cart {
  id: number | null;
  items: CartItem[];
  totalAmount: number;
}
