package com.shop.catalog;

import com.shop.catalog.dto.ProductFilter;
import com.shop.catalog.dto.ProductResponse;
import com.shop.catalog.dto.ProductSummaryResponse;
import com.shop.common.dto.PageResponse;
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
public class ProductController {

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
    return productService.searchPublic(filter, pageable);
  }

  @GetMapping("/{slug}")
  public ProductResponse getBySlug(@PathVariable String slug) {
    return productService.getPublicBySlug(slug);
  }
}
