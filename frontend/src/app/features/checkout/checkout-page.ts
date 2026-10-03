import { DecimalPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { Address } from '../../core/models/address.model';
import { ApiError } from '../../core/models/page.model';
import { AddressService } from '../../core/services/address.service';
import { CartService } from '../../core/services/cart.service';
import { OrderService } from '../../core/services/order.service';

@Component({
  selector: 'app-checkout-page',
  standalone: true,
  imports: [RouterLink, DecimalPipe],
  templateUrl: './checkout-page.html',
  styleUrl: './checkout-page.scss',
})
export class CheckoutPage implements OnInit {
  private readonly addressService = inject(AddressService);
  private readonly orderService = inject(OrderService);
  private readonly cartService = inject(CartService);
  private readonly router = inject(Router);

  protected readonly cart = this.cartService.cart;
  protected readonly addresses = signal<Address[]>([]);
  protected readonly selectedAddressId = signal<number | null>(null);
  protected readonly loading = signal(true);
  protected readonly submitting = signal(false);
  protected readonly errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    this.cartService.refresh().subscribe();
    this.addressService.list().subscribe((addresses) => {
      this.addresses.set(addresses);
      const defaultAddress = addresses.find((a) => a.defaultAddress) ?? addresses[0];
      this.selectedAddressId.set(defaultAddress?.id ?? null);
      this.loading.set(false);
    });
  }

  confirm(): void {
    const addressId = this.selectedAddressId();
    if (!addressId) {
      return;
    }

    this.submitting.set(true);
    this.errorMessage.set(null);
    this.orderService.checkout(addressId).subscribe({
      next: (order) => {
        this.submitting.set(false);
        this.cartService.refresh().subscribe();
        this.router.navigate(['/profile/orders', order.id]);
      },
      error: (err: HttpErrorResponse) => {
        this.submitting.set(false);
        const apiError = err.error as ApiError | undefined;
        this.errorMessage.set(apiError?.message ?? 'An error occurred while placing the order');
      },
    });
  }
}
