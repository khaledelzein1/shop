import { HttpClient } from '@angular/common/http';
import { Injectable, computed, signal, inject } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Cart } from '../models/cart.model';

const EMPTY_CART: Cart = { id: null, items: [], totalAmount: 0 };

@Injectable({ providedIn: 'root' })
export class CartService {
  private readonly http = inject(HttpClient);

  private readonly cartSignal = signal<Cart>(EMPTY_CART);

  readonly cart = this.cartSignal.asReadonly();
  readonly itemCount = computed(() =>
    this.cartSignal().items.reduce((sum, item) => sum + item.quantity, 0),
  );

  refresh(): Observable<Cart> {
    return this.http
      .get<Cart>(`${environment.apiUrl}/me/cart`)
      .pipe(tap((cart) => this.cartSignal.set(cart)));
  }

  addItem(variantId: number, quantity: number): Observable<Cart> {
    return this.http
      .post<Cart>(`${environment.apiUrl}/me/cart/items`, { variantId, quantity })
      .pipe(tap((cart) => this.cartSignal.set(cart)));
  }

  updateItemQuantity(itemId: number, quantity: number): Observable<Cart> {
    return this.http
      .put<Cart>(`${environment.apiUrl}/me/cart/items/${itemId}`, { quantity })
      .pipe(tap((cart) => this.cartSignal.set(cart)));
  }

  removeItem(itemId: number): Observable<Cart> {
    return this.http
      .delete<Cart>(`${environment.apiUrl}/me/cart/items/${itemId}`)
      .pipe(tap((cart) => this.cartSignal.set(cart)));
  }

  clearLocal(): void {
    this.cartSignal.set(EMPTY_CART);
  }
}
