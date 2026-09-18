package com.shop.catalog;

import com.shop.catalog.dto.ProductFilter;
import com.shop.catalog.dto.ProductResponse;
import com.shop.catalog.dto.ProductSummaryResponse;
import com.shop.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Catalogue public — uniquement les produits actifs. */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(
    name = "Catalogue - Produits",
    description = "Lecture publique, uniquement les produits actifs")
public class ProductController {

  private final ProductService productService;

  @GetMapping
  @Operation(
      summary = "Rechercher des produits",
      description = "Filtres combinables, pagination et tri standard Spring (page, size, sort).")
  public PageResponse<ProductSummaryResponse> search(
      @Parameter(description = "Slug de catégorie, ex: informatique")
          @RequestParam(required = false)
          String category,
      @RequestParam(required = false) String brand,
      @RequestParam(required = false) BigDecimal minPrice,
      @RequestParam(required = false) BigDecimal maxPrice,
      @RequestParam(required = false) Boolean inStock,
      @RequestParam(required = false) String q,
      @PageableDefault(size = 20, sort = "name") Pageable pageable) {
    ProductFilter filter = new ProductFilter(category, brand, minPrice, maxPrice, inStock, q);
    return productService.searchPublic(filter, pageable);
  }

  @GetMapping("/{slug}")
  @Operation(
      summary = "Détail d'un produit",
      description = "404 si le produit n'existe pas ou est inactif.")
  public ProductResponse getBySlug(@PathVariable String slug) {
    return productService.getPublicBySlug(slug);
  }
}
