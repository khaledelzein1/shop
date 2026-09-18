package com.shop.catalog;

import com.shop.catalog.dto.ProductImageRequest;
import com.shop.catalog.dto.ProductImageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
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
public class ProductImageAdminController {

    private final ProductImageService productImageService;

    @PostMapping("/products/{productId}/images")
    public ResponseEntity<ProductImageResponse> create(
            @PathVariable Long productId, @Valid @RequestBody ProductImageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productImageService.create(productId, request));
    }

    @PutMapping("/products/{productId}/images/{imageId}")
    public ProductImageResponse update(
            @PathVariable Long productId,
            @PathVariable Long imageId,
            @Valid @RequestBody ProductImageRequest request) {
        return productImageService.update(productId, imageId, request);
    }

    @DeleteMapping("/products/{productId}/images/{imageId}")
    public ResponseEntity<Void> delete(@PathVariable Long productId, @PathVariable Long imageId) {
        productImageService.delete(productId, imageId);
        return ResponseEntity.noContent().build();
    }
}
