import { DecimalPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { environment } from '../../../environments/environment';
import { Address } from '../../core/models/address.model';
import { CardDetails, PaymentProvider } from '../../core/models/order.model';
import { ApiError } from '../../core/models/page.model';
import { AddressService } from '../../core/services/address.service';
import { CartService } from '../../core/services/cart.service';
import { OrderService } from '../../core/services/order.service';
import {
  cvcLength,
  detectBrand,
  digitsOnly,
  formatCardNumber,
  formatExpiry,
  isExpired,
  maxCardDigits,
  parseExpiry,
  passesLuhn,
} from './card-utils';

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
  /** Les rappels de cartes de test ne sont affichés qu'en dev. */
  protected readonly production = environment.production;
  protected readonly addresses = signal<Address[]>([]);
  protected readonly selectedAddressId = signal<number | null>(null);
  protected readonly loading = signal(true);
  protected readonly submitting = signal(false);
  protected readonly errorMessage = signal<string | null>(null);

  /** STRIPE : redirection vers la page Stripe. DEMO : formulaire de carte ci-dessous. */
  protected readonly provider = signal<PaymentProvider | null>(null);

  // --- Formulaire de carte (mode DEMO) ---
  protected readonly holderName = signal('');
  protected readonly cardNumber = signal('');
  protected readonly expiry = signal('');
  protected readonly cvc = signal('');
  /** Les erreurs ne s'affichent qu'après une première tentative de paiement. */
  protected readonly showErrors = signal(false);

  protected readonly brand = computed(() => detectBrand(digitsOnly(this.cardNumber())));
  protected readonly cvcMaxLength = computed(() => cvcLength(this.brand()));
  protected readonly cardErrors = computed(() => {
    const digits = digitsOnly(this.cardNumber());
    const expiry = parseExpiry(this.expiry());
    return {
      holderName: this.holderName().trim() ? null : 'Enter the name on your card.',
      number: !digits
        ? 'Enter your card number.'
        : passesLuhn(digits)
          ? null
          : 'Your card number is invalid.',
      expiry: !expiry
        ? 'Enter a valid expiry date (MM / YY).'
        : isExpired(expiry)
          ? 'Your card has expired.'
          : null,
      cvc: new RegExp(`^\\d{${this.cvcMaxLength()}}$`).test(this.cvc())
        ? null
        : `Enter the ${this.cvcMaxLength()}-digit security code.`,
    };
  });
  protected readonly cardValid = computed(() => Object.values(this.cardErrors()).every((e) => e === null));

  ngOnInit(): void {
    this.cartService.refresh().subscribe();
    this.orderService.paymentProvider().subscribe(({ provider }) => this.provider.set(provider));
    this.addressService.list().subscribe((addresses) => {
      this.addresses.set(addresses);
      const defaultAddress = addresses.find((a) => a.defaultAddress) ?? addresses[0];
      this.selectedAddressId.set(defaultAddress?.id ?? null);
      this.loading.set(false);
    });
  }

  onCardNumberInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    const digits = digitsOnly(input.value);
    const formatted = formatCardNumber(digits.slice(0, maxCardDigits(detectBrand(digits))));
    this.cardNumber.set(formatted);
    input.value = formatted;
  }

  onExpiryInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    // Effacer le " / " ne doit pas le faire réapparaître aussitôt.
    const deleting = (event as InputEvent).inputType?.startsWith('delete');
    const digits = digitsOnly(input.value);
    const formatted = deleting && digits.length === 2 ? digits : formatExpiry(digits);
    this.expiry.set(formatted);
    input.value = formatted;
  }

  onCvcInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    const digits = digitsOnly(input.value).slice(0, this.cvcMaxLength());
    this.cvc.set(digits);
    input.value = digits;
  }

  onHolderNameInput(event: Event): void {
    this.holderName.set((event.target as HTMLInputElement).value);
  }

  pay(): void {
    const addressId = this.selectedAddressId();
    if (!addressId) {
      return;
    }

    let card: CardDetails | undefined;
    if (this.provider() === 'DEMO') {
      this.showErrors.set(true);
      if (!this.cardValid()) {
        return;
      }
      const expiry = parseExpiry(this.expiry())!;
      card = {
        number: digitsOnly(this.cardNumber()),
        expMonth: expiry.month,
        expYear: expiry.year,
        cvc: this.cvc(),
        holderName: this.holderName().trim(),
      };
    }

    this.submitting.set(true);
    this.errorMessage.set(null);
    this.orderService.checkout(addressId, card).subscribe({
      next: ({ order, paymentUrl }) => {
        if (paymentUrl) {
          // Mode STRIPE : sortie de l'appli, `submitting` reste vrai pendant le chargement de Stripe.
          window.location.href = paymentUrl;
          return;
        }
        // Mode DEMO : la commande est déjà payée et confirmée.
        this.cartService.refresh().subscribe();
        this.router.navigate(['/checkout/success'], { queryParams: { order_id: order.id } });
      },
      error: (err: HttpErrorResponse) => {
        this.submitting.set(false);
        const apiError = err.error as ApiError | undefined;
        this.errorMessage.set(apiError?.message ?? 'An error occurred while placing the order');
      },
    });
  }
}
