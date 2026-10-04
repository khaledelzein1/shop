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
  /** Carte utilisée, ex. "Visa" + "4242" — null si payée via la page Stripe. */
  cardBrand: string | null;
  cardLast4: string | null;
}

export interface OrderSummary {
  id: number;
  status: OrderStatus;
  totalAmount: number;
  createdAt: string;
  itemCount: number;
}

export interface AdminOrderSummary extends OrderSummary {
  userEmail: string;
}

/** Réponse du checkout : commande PENDING + page de paiement Stripe vers laquelle rediriger. */
export interface CheckoutResponse {
  order: Order;
  /** URL de la page Stripe (mode STRIPE) ; null en mode DEMO, où la commande est déjà payée. */
  paymentUrl: string | null;
}

/** Mode de paiement actif côté backend : page Stripe, ou formulaire de carte intégré (démo). */
export type PaymentProvider = 'STRIPE' | 'DEMO';

/** Carte envoyée au checkout en mode DEMO — jamais stockée, seuls marque et 4 derniers chiffres reviennent. */
export interface CardDetails {
  number: string;
  expMonth: number;
  expYear: number;
  cvc: string;
  holderName: string;
}
