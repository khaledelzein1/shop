package com.shop.catalog.dto;

import com.shop.catalog.ProductImage;

public record ProductImageResponse(
    Long id, String url, int position, boolean primary, Long variantId) {

  public static ProductImageResponse from(ProductImage image) {
    return new ProductImageResponse(
        image.getId(),
        image.getUrl(),
        image.getPosition(),
        image.isPrimary(),
        image.getVariant() != null ? image.getVariant().getId() : null);
  }
}
