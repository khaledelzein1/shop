import { DecimalPipe, KeyValuePipe } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CartService } from '../../core/services/cart.service';

@Component({
  selector: 'app-cart-page',
  standalone: true,
  imports: [RouterLink, DecimalPipe, KeyValuePipe, FormsModule],
  templateUrl: './cart-page.html',
  styleUrl: './cart-page.scss',
})
export class CartPage implements OnInit {
  private readonly cartService = inject(CartService);
  protected readonly cart = this.cartService.cart;

  ngOnInit(): void {
    this.cartService.refresh().subscribe();
  }

  updateQuantity(itemId: number, quantity: number): void {
    if (quantity < 1) {
      return;
    }
    this.cartService.updateItemQuantity(itemId, quantity).subscribe();
  }

  removeItem(itemId: number): void {
    this.cartService.removeItem(itemId).subscribe();
  }
}
