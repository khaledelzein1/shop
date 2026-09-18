import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Category } from '../models/category.model';
import { PageResponse } from '../models/page.model';
import { Product, ProductFilter, ProductSummary } from '../models/product.model';

@Injectable({ providedIn: 'root' })
export class CatalogService {
  constructor(private readonly http: HttpClient) {}

  listCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(`${environment.apiUrl}/categories`);
  }

  searchProducts(filter: ProductFilter, page: number, size: number): Observable<PageResponse<ProductSummary>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (filter.category) params = params.set('category', filter.category);
    if (filter.brand) params = params.set('brand', filter.brand);
    if (filter.minPrice != null) params = params.set('minPrice', filter.minPrice);
    if (filter.maxPrice != null) params = params.set('maxPrice', filter.maxPrice);
    if (filter.inStock != null) params = params.set('inStock', filter.inStock);
    if (filter.q) params = params.set('q', filter.q);

    return this.http.get<PageResponse<ProductSummary>>(`${environment.apiUrl}/products`, { params });
  }

  getProductBySlug(slug: string): Observable<Product> {
    return this.http.get<Product>(`${environment.apiUrl}/products/${slug}`);
  }
}
