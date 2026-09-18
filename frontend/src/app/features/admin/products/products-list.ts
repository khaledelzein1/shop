import { DecimalPipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
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

  ngOnInit(): void {
    this.load();
  }

  private load(): void {
    this.loading.set(true);
    this.productService.search({}, 0, 50).subscribe((res) => {
      this.products.set(res.content);
      this.loading.set(false);
    });
  }
}
