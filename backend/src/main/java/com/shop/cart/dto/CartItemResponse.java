package com.shop.cart.dto;

import com.shop.cart.CartItem;
import com.shop.catalog.ProductVariant;
import java.math.BigDecimal;
import java.util.Map;

public record CartItemResponse(
        Long id,
        Long variantId,
        String productName,
        String productSlug,
        String sku,
        Map<String, String> attributes,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal lineTotal) {

    public static CartItemResponse from(CartItem item) {
        ProductVariant variant = item.getVariant();
        BigDecimal lineTotal = item.getUnitPriceSnapshot().multiply(BigDecimal.valueOf(item.getQuantity()));
        return new CartItemResponse(
                item.getId(),
                variant.getId(),
                variant.getProduct().getName(),
                variant.getProduct().getSlug(),
                variant.getSku(),
                variant.getAttributes(),
                item.getUnitPriceSnapshot(),
                item.getQuantity(),
                lineTotal);
    }
}
