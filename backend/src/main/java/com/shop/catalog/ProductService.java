package com.shop.catalog;

import com.shop.catalog.dto.ProductFilter;
import com.shop.catalog.dto.ProductRequest;
import com.shop.catalog.dto.ProductResponse;
import com.shop.catalog.dto.ProductSummaryResponse;
import com.shop.catalog.spec.ProductSpecifications;
import com.shop.common.dto.PageResponse;
import com.shop.common.exception.ConflictException;
import com.shop.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;

  /** Catalogue public : uniquement les produits actifs. */
  @Transactional(readOnly = true)
  public PageResponse<ProductSummaryResponse> searchPublic(
      ProductFilter filter, Pageable pageable) {
    Specification<Product> spec = buildSpecification(filter, true);
    Page<Product> page = productRepository.findAll(spec, pageable);
    return PageResponse.from(page.map(ProductSummaryResponse::from));
  }

  @Transactional(readOnly = true)
  public ProductResponse getPublicBySlug(String slug) {
    Product product =
        productRepository
            .findBySlugAndActiveTrue(slug)
            .orElseThrow(
                () -> new ResourceNotFoundException("Produit introuvable (slug=" + slug + ")"));
    return ProductResponse.from(product);
  }

  /** Listing admin : tous les produits (actifs et inactifs). */
  @Transactional(readOnly = true)
  public PageResponse<ProductSummaryResponse> searchAdmin(ProductFilter filter, Pageable pageable) {
    Specification<Product> spec = buildSpecification(filter, null);
    Page<Product> page = productRepository.findAll(spec, pageable);
    return PageResponse.from(page.map(ProductSummaryResponse::from));
  }

  @Transactional(readOnly = true)
  public ProductResponse getByIdForAdmin(Long id) {
    return ProductResponse.from(findByIdOrThrow(id));
  }

  @Transactional
  public ProductResponse create(ProductRequest request) {
    if (productRepository.existsBySlug(request.slug())) {
      throw new ConflictException("Un produit existe déjà avec le slug : " + request.slug());
    }
    Category category =
        categoryRepository
            .findById(request.categoryId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Catégorie introuvable (id=" + request.categoryId() + ")"));

    Product product = new Product();
    applyRequest(product, request, category);
    return ProductResponse.from(productRepository.save(product));
  }

  @Transactional
  public ProductResponse update(Long id, ProductRequest request) {
    Product product = findByIdOrThrow(id);
    if (!product.getSlug().equals(request.slug())
        && productRepository.existsBySlug(request.slug())) {
      throw new ConflictException("Un produit existe déjà avec le slug : " + request.slug());
    }
    Category category =
        categoryRepository
            .findById(request.categoryId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Catégorie introuvable (id=" + request.categoryId() + ")"));

    applyRequest(product, request, category);
    return ProductResponse.from(product);
  }

  @Transactional
  public void delete(Long id) {
    Product product = findByIdOrThrow(id);
    productRepository.delete(product);
  }

  private Specification<Product> buildSpecification(ProductFilter filter, Boolean forceActive) {
    Specification<Product> spec = Specification.where(null);
    if (forceActive != null) {
      spec = spec.and(ProductSpecifications.isActive(forceActive));
    }
    return spec.and(ProductSpecifications.hasCategorySlug(filter.category()))
        .and(ProductSpecifications.hasBrand(filter.brand()))
        .and(ProductSpecifications.priceBetween(filter.minPrice(), filter.maxPrice()))
        .and(ProductSpecifications.inStockOnly(filter.inStock()))
        .and(ProductSpecifications.nameOrDescriptionContains(filter.q()));
  }

  private void applyRequest(Product product, ProductRequest request, Category category) {
    product.setName(request.name());
    product.setSlug(request.slug());
    product.setDescription(request.description());
    product.setBrand(request.brand());
    product.setActive(request.active());
    product.setCategory(category);
  }

  Product findByIdOrThrow(Long id) {
    return productRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable (id=" + id + ")"));
  }
}
