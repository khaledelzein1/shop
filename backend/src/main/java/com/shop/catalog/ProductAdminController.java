package com.shop.catalog;

import com.shop.catalog.dto.ProductFilter;
import com.shop.catalog.dto.ProductRequest;
import com.shop.catalog.dto.ProductResponse;
import com.shop.catalog.dto.ProductSummaryResponse;
import com.shop.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Gestion admin du catalogue : contrairement à {@link ProductController}, expose aussi les produits
 * inactifs (brouillons/désactivés) — d'où un chemin séparé plutôt qu'un paramètre conditionnel sur
 * l'endpoint public.
 */
@RestController
@RequestMapping("/api/admin/products")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Produits", description = "CRUD produits (ADMIN)")
@RequiredArgsConstructor
public class ProductAdminController {

  private final ProductService productService;

  @GetMapping
  public PageResponse<ProductSummaryResponse> search(
      @RequestParam(required = false) String category,
      @RequestParam(required = false) String brand,
      @RequestParam(required = false) BigDecimal minPrice,
      @RequestParam(required = false) BigDecimal maxPrice,
      @RequestParam(required = false) Boolean inStock,
      @RequestParam(required = false) String q,
      @PageableDefault(size = 20, sort = "name") Pageable pageable) {
    ProductFilter filter = new ProductFilter(category, brand, minPrice, maxPrice, inStock, q);
    return productService.searchAdmin(filter, pageable);
  }

  @GetMapping("/{id}")
  public ProductResponse getById(@PathVariable Long id) {
    return productService.getByIdForAdmin(id);
  }

  @PostMapping
  public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(request));
  }

  @PutMapping("/{id}")
  public ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
    return productService.update(id, request);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    productService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
