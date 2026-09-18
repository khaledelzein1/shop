package com.shop.catalog;

import com.shop.catalog.dto.ProductImageRequest;
import com.shop.catalog.dto.ProductImageResponse;
import com.shop.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductImageService {

    private final ProductImageRepository imageRepository;
    private final ProductService productService;

    @Transactional
    public ProductImageResponse create(Long productId, ProductImageRequest request) {
        Product product = productService.findByIdOrThrow(productId);

        ProductImage image = new ProductImage();
        applyRequest(image, request);
        image.setProduct(product);
        product.getImages().add(image);

        return ProductImageResponse.from(imageRepository.save(image));
    }

    @Transactional
    public ProductImageResponse update(Long productId, Long imageId, ProductImageRequest request) {
        ProductImage image = findByIdAndProductOrThrow(imageId, productId);
        applyRequest(image, request);
        return ProductImageResponse.from(image);
    }

    @Transactional
    public void delete(Long productId, Long imageId) {
        ProductImage image = findByIdAndProductOrThrow(imageId, productId);
        image.getProduct().getImages().remove(image);
        imageRepository.delete(image);
    }

    private void applyRequest(ProductImage image, ProductImageRequest request) {
        image.setUrl(request.url());
        image.setPosition(request.position());
        image.setPrimary(request.primary());
    }

    private ProductImage findByIdAndProductOrThrow(Long imageId, Long productId) {
        return imageRepository.findByIdAndProductId(imageId, productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Image introuvable (id=" + imageId + ") pour le produit " + productId));
    }
}
