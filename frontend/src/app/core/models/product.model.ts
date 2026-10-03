import { Category } from './category.model';

export interface ProductSummary {
  id: number;
  name: string;
  slug: string;
  brand: string | null;
  categoryName: string;
  minPrice: number | null;
  maxPrice: number | null;
  inStock: boolean;
  active: boolean;
  primaryImageUrl: string | null;
}

export interface ProductVariant {
  id: number;
  sku: string;
  price: number;
  stock: number;
  active: boolean;
  attributes: Record<string, string>;
}

export interface ProductImage {
  id: number;
  url: string;
  position: number;
  primary: boolean;
  variantId: number | null;
}

export interface Product {
  id: number;
  name: string;
  slug: string;
  description: string | null;
  brand: string | null;
  active: boolean;
  category: Category;
  variants: ProductVariant[];
  images: ProductImage[];
}

export interface ProductFilter {
  category?: string;
  brand?: string;
  minPrice?: number;
  maxPrice?: number;
  inStock?: boolean;
  q?: string;
}

export interface ProductRequest {
  name: string;
  slug: string;
  description: string;
  brand: string;
  active: boolean;
  categoryId: number;
}

export interface ProductVariantRequest {
  sku: string;
  price: number;
  stock: number;
  active: boolean;
  attributes: Record<string, string>;
}

export interface ProductImageRequest {
  url: string;
  position: number;
  primary: boolean;
  variantId: number | null;
}

export interface UpdateStockRequest {
  stock: number;
}
