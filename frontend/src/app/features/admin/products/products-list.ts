import { DecimalPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ApiError } from '../../../core/models/page.model';
import { ProductSummary } from '../../../core/models/product.model';
import { AdminProductService } from '../../../core/services/admin-product.service';

@Component({
  selector: 'app-admin-products-list',
  standalone: true,
  imports: [RouterLink, DecimalPipe],
  templateUrl: './products-list.html',
})
export class AdminProductsList implements OnInit {
  private readonly productService = inject(AdminProductService);

  protected readonly products = signal<ProductSummary[]>([]);
  protected readonly loading = signal(true);
  protected readonly deletingId = signal<number | null>(null);
  protected readonly errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    this.load();
  }

  deleteProduct(product: ProductSummary): void {
    const confirmed = confirm(
      `Delete "${product.name}" with all its variants and photos?\n\nPast orders keep their details. This cannot be undone.`,
    );
    if (!confirmed) {
      return;
    }
    this.deletingId.set(product.id);
    this.errorMessage.set(null);
    this.productService.delete(product.id).subscribe({
      next: () => {
        this.products.update((list) => list.filter((p) => p.id !== product.id));
        this.deletingId.set(null);
      },
      error: (err: HttpErrorResponse) => {
        this.deletingId.set(null);
        this.errorMessage.set((err.error as ApiError | undefined)?.message ?? 'Could not delete this product');
      },
    });
  }

  private load(): void {
    this.loading.set(true);
    this.productService.search({}, 0, 50).subscribe((res) => {
      this.products.set(res.content);
      this.loading.set(false);
    });
  }
}
