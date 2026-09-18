import { DecimalPipe, KeyValuePipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Product, ProductVariant } from '../../../core/models/product.model';
import { AuthService } from '../../../core/services/auth.service';
import { CartService } from '../../../core/services/cart.service';
import { CatalogService } from '../../../core/services/catalog.service';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [RouterLink, DecimalPipe, KeyValuePipe, FormsModule],
  templateUrl: './product-detail.html',
  styleUrl: './product-detail.scss',
})
export class ProductDetail implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly catalogService = inject(CatalogService);
  private readonly cartService = inject(CartService);
  private readonly router = inject(Router);
  protected readonly auth = inject(AuthService);

  protected readonly product = signal<Product | null>(null);
  protected readonly selectedVariant = signal<ProductVariant | null>(null);
  protected readonly quantity = signal(1);
  protected readonly loading = signal(true);
  protected readonly addingToCart = signal(false);
  protected readonly addedMessage = signal(false);

  ngOnInit(): void {
    const slug = this.route.snapshot.paramMap.get('slug')!;
    this.catalogService.getProductBySlug(slug).subscribe((product) => {
      this.product.set(product);
      const firstAvailable = product.variants.find((v) => v.active && v.stock > 0) ?? product.variants[0] ?? null;
      this.selectedVariant.set(firstAvailable);
      this.loading.set(false);
    });
  }

  selectVariant(variant: ProductVariant): void {
    this.selectedVariant.set(variant);
    this.quantity.set(1);
  }

  addToCart(): void {
    const variant = this.selectedVariant();
    if (!variant) {
      return;
    }
    if (!this.auth.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }

    this.addingToCart.set(true);
    this.cartService.addItem(variant.id, this.quantity()).subscribe({
      next: () => {
        this.addingToCart.set(false);
        this.addedMessage.set(true);
        setTimeout(() => this.addedMessage.set(false), 2000);
      },
      error: () => this.addingToCart.set(false),
    });
  }
}
