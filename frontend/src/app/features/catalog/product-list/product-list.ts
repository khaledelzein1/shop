import { DecimalPipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Category } from '../../../core/models/category.model';
import { ProductSummary } from '../../../core/models/product.model';
import { CatalogService } from '../../../core/services/catalog.service';

const PAGE_SIZE = 12;

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [RouterLink, FormsModule, DecimalPipe],
  templateUrl: './product-list.html',
  styleUrl: './product-list.scss',
})
export class ProductList implements OnInit {
  private readonly catalogService = inject(CatalogService);

  protected readonly categories = signal<Category[]>([]);
  protected readonly products = signal<ProductSummary[]>([]);
  protected readonly loading = signal(false);
  protected readonly page = signal(0);
  protected readonly totalPages = signal(0);
  protected readonly pageNumbers = computed(() => Array.from({ length: this.totalPages() }, (_, i) => i));

  protected selectedCategory = '';
  protected searchQuery = '';
  protected onlyInStock = false;

  ngOnInit(): void {
    this.catalogService.listCategories().subscribe((cats) => this.categories.set(cats));
    this.search();
  }

  onFilterChange(): void {
    this.page.set(0);
    this.search();
  }

  goToPage(p: number): void {
    this.page.set(p);
    this.search();
  }

  private search(): void {
    this.loading.set(true);
    this.catalogService
      .searchProducts(
        {
          category: this.selectedCategory || undefined,
          q: this.searchQuery || undefined,
          inStock: this.onlyInStock || undefined,
        },
        this.page(),
        PAGE_SIZE,
      )
      .subscribe((res) => {
        this.products.set(res.content);
        this.totalPages.set(res.totalPages);
        this.loading.set(false);
      });
  }
}
