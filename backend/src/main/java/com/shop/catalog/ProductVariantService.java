package com.shop.catalog;

import com.shop.catalog.dto.ProductVariantRequest;
import com.shop.catalog.dto.ProductVariantResponse;
import com.shop.common.exception.ConflictException;
import com.shop.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductVariantService {

    private final ProductVariantRepository variantRepository;
    private final ProductService productService;

    @Transactional
    public ProductVariantResponse create(Long productId, ProductVariantRequest request) {
        Product product = productService.findByIdOrThrow(productId);
        if (variantRepository.existsBySku(request.sku())) {
            throw new ConflictException("Une variante existe déjà avec le SKU : " + request.sku());
        }

        ProductVariant variant = new ProductVariant();
        applyRequest(variant, request);
        variant.setProduct(product);
        product.getVariants().add(variant);

        return ProductVariantResponse.from(variantRepository.save(variant));
    }

    @Transactional
    public ProductVariantResponse update(Long productId, Long variantId, ProductVariantRequest request) {
        ProductVariant variant = findByIdAndProductOrThrow(variantId, productId);
        if (!variant.getSku().equals(request.sku()) && variantRepository.existsBySku(request.sku())) {
            throw new ConflictException("Une variante existe déjà avec le SKU : " + request.sku());
        }
        applyRequest(variant, request);
        return ProductVariantResponse.from(variant);
    }

    @Transactional
    public void delete(Long productId, Long variantId) {
        ProductVariant variant = findByIdAndProductOrThrow(variantId, productId);
        variant.getProduct().getVariants().remove(variant);
        variantRepository.delete(variant);
    }

    private void applyRequest(ProductVariant variant, ProductVariantRequest request) {
        variant.setSku(request.sku());
        variant.setPrice(request.price());
        variant.setStock(request.stock());
        variant.setActive(request.active());
        variant.setAttributes(request.attributes() != null ? request.attributes() : variant.getAttributes());
    }

    private ProductVariant findByIdAndProductOrThrow(Long variantId, Long productId) {
        return variantRepository.findByIdAndProductId(variantId, productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Variante introuvable (id=" + variantId + ") pour le produit " + productId));
    }
}
