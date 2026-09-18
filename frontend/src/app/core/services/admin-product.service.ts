import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PageResponse } from '../models/page.model';
import {
  Product,
  ProductFilter,
  ProductImage,
  ProductImageRequest,
  ProductRequest,
  ProductSummary,
  ProductVariant,
  ProductVariantRequest,
  UpdateStockRequest,
} from '../models/product.model';

@Injectable({ providedIn: 'root' })
export class AdminProductService {
  constructor(private readonly http: HttpClient) {}

  search(filter: ProductFilter, page: number, size: number): Observable<PageResponse<ProductSummary>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (filter.category) params = params.set('category', filter.category);
    if (filter.brand) params = params.set('brand', filter.brand);
    if (filter.q) params = params.set('q', filter.q);

    return this.http.get<PageResponse<ProductSummary>>(`${environment.apiUrl}/admin/products`, { params });
  }

  getById(id: number): Observable<Product> {
    return this.http.get<Product>(`${environment.apiUrl}/admin/products/${id}`);
  }

  create(payload: ProductRequest): Observable<Product> {
    return this.http.post<Product>(`${environment.apiUrl}/admin/products`, payload);
  }

  update(id: number, payload: ProductRequest): Observable<Product> {
    return this.http.put<Product>(`${environment.apiUrl}/admin/products/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/admin/products/${id}`);
  }

  addVariant(productId: number, payload: ProductVariantRequest): Observable<ProductVariant> {
    return this.http.post<ProductVariant>(`${environment.apiUrl}/admin/products/${productId}/variants`, payload);
  }

  updateVariant(productId: number, variantId: number, payload: ProductVariantRequest): Observable<ProductVariant> {
    return this.http.put<ProductVariant>(
      `${environment.apiUrl}/admin/products/${productId}/variants/${variantId}`,
      payload,
    );
  }

  updateVariantStock(productId: number, variantId: number, payload: UpdateStockRequest): Observable<ProductVariant> {
    return this.http.patch<ProductVariant>(
      `${environment.apiUrl}/admin/products/${productId}/variants/${variantId}/stock`,
      payload,
    );
  }

  deleteVariant(productId: number, variantId: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/admin/products/${productId}/variants/${variantId}`);
  }

  addImage(productId: number, payload: ProductImageRequest): Observable<ProductImage> {
    return this.http.post<ProductImage>(`${environment.apiUrl}/admin/products/${productId}/images`, payload);
  }

  deleteImage(productId: number, imageId: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/admin/products/${productId}/images/${imageId}`);
  }
}
