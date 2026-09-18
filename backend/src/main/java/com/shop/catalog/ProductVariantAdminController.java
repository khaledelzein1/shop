package com.shop.catalog;

import com.shop.catalog.dto.ProductVariantRequest;
import com.shop.catalog.dto.ProductVariantResponse;
import com.shop.catalog.dto.UpdateStockRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class ProductVariantAdminController {

    private final ProductVariantService productVariantService;

    @PostMapping("/products/{productId}/variants")
    public ResponseEntity<ProductVariantResponse> create(
            @PathVariable Long productId, @Valid @RequestBody ProductVariantRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productVariantService.create(productId, request));
    }

    @PutMapping("/products/{productId}/variants/{variantId}")
    public ProductVariantResponse update(
            @PathVariable Long productId,
            @PathVariable Long variantId,
            @Valid @RequestBody ProductVariantRequest request) {
        return productVariantService.update(productId, variantId, request);
    }

    @PatchMapping("/products/{productId}/variants/{variantId}/stock")
    public ProductVariantResponse updateStock(
            @PathVariable Long productId,
            @PathVariable Long variantId,
            @Valid @RequestBody UpdateStockRequest request) {
        return productVariantService.updateStock(productId, variantId, request);
    }

    @DeleteMapping("/products/{productId}/variants/{variantId}")
    public ResponseEntity<Void> delete(@PathVariable Long productId, @PathVariable Long variantId) {
        productVariantService.delete(productId, variantId);
        return ResponseEntity.noContent().build();
    }
}
