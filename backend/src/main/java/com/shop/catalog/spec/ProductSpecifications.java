package com.shop.catalog.spec;

import com.shop.catalog.Product;
import com.shop.catalog.ProductVariant;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import java.math.BigDecimal;
import org.springframework.data.jpa.domain.Specification;

/**
 * Prédicats dynamiques pour la recherche catalogue. Chaque méthode renvoie {@code null} quand le
 * filtre n'est pas fourni — {@link Specification#and} ignore nativement les specs nulles, donc on
 * peut les chaîner sans if/else.
 */
public final class ProductSpecifications {

  private ProductSpecifications() {}

  public static Specification<Product> isActive(boolean active) {
    return (root, query, cb) -> cb.equal(root.get("active"), active);
  }

  public static Specification<Product> hasCategorySlug(String categorySlug) {
    if (categorySlug == null || categorySlug.isBlank()) {
      return null;
    }
    return (root, query, cb) -> cb.equal(root.join("category").get("slug"), categorySlug);
  }

  public static Specification<Product> hasBrand(String brand) {
    if (brand == null || brand.isBlank()) {
      return null;
    }
    return (root, query, cb) -> cb.equal(cb.lower(root.get("brand")), brand.toLowerCase());
  }

  public static Specification<Product> nameOrDescriptionContains(String q) {
    if (q == null || q.isBlank()) {
      return null;
    }
    String like = "%" + q.toLowerCase() + "%";
    return (root, query, cb) ->
        cb.or(
            cb.like(cb.lower(root.get("name")), like),
            cb.like(cb.lower(cb.coalesce(root.get("description"), "")), like));
  }

  public static Specification<Product> priceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
    if (minPrice == null && maxPrice == null) {
      return null;
    }
    return (root, query, cb) -> {
      query.distinct(true);
      Join<Product, ProductVariant> variants = root.join("variants");
      if (minPrice != null && maxPrice != null) {
        return cb.between(variants.get("price"), minPrice, maxPrice);
      }
      if (minPrice != null) {
        return cb.greaterThanOrEqualTo(variants.get("price"), minPrice);
      }
      return cb.lessThanOrEqualTo(variants.get("price"), maxPrice);
    };
  }

  public static Specification<Product> inStockOnly(Boolean inStock) {
    if (inStock == null || !inStock) {
      return null;
    }
    return (root, query, cb) -> {
      query.distinct(true);
      Join<Product, ProductVariant> variants = root.join("variants", JoinType.LEFT);
      return cb.and(cb.isTrue(variants.get("active")), cb.greaterThan(variants.get("stock"), 0));
    };
  }
}
