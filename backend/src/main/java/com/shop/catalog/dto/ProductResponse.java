package com.shop.catalog.dto;

import com.shop.catalog.Product;
import java.util.List;

/** Vue détail d'un produit (page produit) : catégorie, variantes et images complètes. */
public record ProductResponse(
        Long id,
        String name,
        String slug,
        String description,
        String brand,
        boolean active,
        CategoryResponse category,
        List<ProductVariantResponse> variants,
        List<ProductImageResponse> images) {

    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getDescription(),
                product.getBrand(),
                product.isActive(),
                CategoryResponse.from(product.getCategory()),
                product.getVariants().stream().map(ProductVariantResponse::from).toList(),
                product.getImages().stream().map(ProductImageResponse::from).toList());
    }
}
