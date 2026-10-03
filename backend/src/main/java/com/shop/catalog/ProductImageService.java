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
  private final ProductVariantRepository variantRepository;

  @Transactional
  public ProductImageResponse create(Long productId, ProductImageRequest request) {
    Product product = productService.findByIdOrThrow(productId);

    ProductImage image = new ProductImage();
    applyRequest(image, request, productId);
    image.setProduct(product);
    product.getImages().add(image);

    return ProductImageResponse.from(imageRepository.save(image));
  }

  @Transactional
  public ProductImageResponse update(Long productId, Long imageId, ProductImageRequest request) {
    ProductImage image = findByIdAndProductOrThrow(imageId, productId);
    applyRequest(image, request, productId);
    return ProductImageResponse.from(image);
  }

  @Transactional
  public void delete(Long productId, Long imageId) {
    ProductImage image = findByIdAndProductOrThrow(imageId, productId);
    image.getProduct().getImages().remove(image);
    imageRepository.delete(image);
  }

  private void applyRequest(ProductImage image, ProductImageRequest request, Long productId) {
    image.setUrl(request.url());
    image.setPosition(request.position());
    image.setPrimary(request.primary());
    if (request.variantId() != null) {
      ProductVariant variant =
          variantRepository
              .findByIdAndProductId(request.variantId(), productId)
              .orElseThrow(
                  () ->
                      new ResourceNotFoundException(
                          "Variant not found (id="
                              + request.variantId()
                              + ") for product "
                              + productId));
      image.setVariant(variant);
    } else {
      image.setVariant(null);
    }
  }

  private ProductImage findByIdAndProductOrThrow(Long imageId, Long productId) {
    return imageRepository
        .findByIdAndProductId(imageId, productId)
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    "Image not found (id=" + imageId + ") for product " + productId));
  }
}
