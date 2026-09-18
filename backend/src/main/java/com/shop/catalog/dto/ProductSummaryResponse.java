package com.shop.catalog.dto;

import com.shop.catalog.Product;
import com.shop.catalog.ProductVariant;
import java.math.BigDecimal;
import java.util.Comparator;

/** Vue allégée pour les listes/grilles catalogue — pas de description ni d'attributs de variante. */
public record ProductSummaryResponse(
        Long id,
        String name,
        String slug,
        String brand,
        String categoryName,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        boolean inStock,
        String primaryImageUrl) {

    public static ProductSummaryResponse from(Product product) {
        BigDecimal minPrice = product.getVariants().stream()
                .map(ProductVariant::getPrice)
                .min(Comparator.naturalOrder())
                .orElse(null);
        BigDecimal maxPrice = product.getVariants().stream()
                .map(ProductVariant::getPrice)
                .max(Comparator.naturalOrder())
                .orElse(null);
        boolean inStock = product.getVariants().stream()
                .anyMatch(v -> v.isActive() && v.getStock() > 0);
        String primaryImageUrl = product.getImages().stream()
                .filter(img -> img.isPrimary())
                .findFirst()
                .or(() -> product.getImages().stream().findFirst())
                .map(img -> img.getUrl())
                .orElse(null);

        return new ProductSummaryResponse(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getBrand(),
                product.getCategory().getName(),
                minPrice,
                maxPrice,
                inStock,
                primaryImageUrl);
    }
}
