import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { environment } from '../../../environments/environment';
import { CartService } from './cart.service';

describe('CartService', () => {
  let service: CartService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(CartService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('starts empty with itemCount 0', () => {
    expect(service.cart().items).toHaveLength(0);
    expect(service.itemCount()).toBe(0);
  });

  it('itemCount sums quantities across all lines after refresh', () => {
    service.refresh().subscribe();

    httpMock.expectOne(`${environment.apiUrl}/me/cart`).flush({
      id: 1,
      totalAmount: 939.97,
      items: [
        { id: 1, variantId: 1, productName: 'Laptop', productSlug: 'laptop', sku: 'L1', attributes: {}, unitPrice: 899.99, quantity: 1, lineTotal: 899.99 },
        { id: 2, variantId: 3, productName: 'T-shirt', productSlug: 'tshirt', sku: 'T1', attributes: {}, unitPrice: 19.99, quantity: 2, lineTotal: 39.98 },
      ],
    });

    expect(service.itemCount()).toBe(3);
    expect(service.cart().totalAmount).toBe(939.97);
  });
});
