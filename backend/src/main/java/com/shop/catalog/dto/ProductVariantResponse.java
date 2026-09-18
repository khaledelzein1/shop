package com.shop.catalog.dto;

import com.shop.catalog.ProductVariant;
import java.math.BigDecimal;
import java.util.Map;

public record ProductVariantResponse(
    Long id,
    String sku,
    BigDecimal price,
    int stock,
    boolean active,
    Map<String, String> attributes) {

  public static ProductVariantResponse from(ProductVariant variant) {
    return new ProductVariantResponse(
        variant.getId(),
        variant.getSku(),
        variant.getPrice(),
        variant.getStock(),
        variant.isActive(),
        variant.getAttributes());
  }
}
