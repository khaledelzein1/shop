package com.shop.catalog;

import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository
    extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

  Optional<Product> findBySlug(String slug);

  Optional<Product> findBySlugAndActiveTrue(String slug);

  boolean existsBySlug(String slug);

  boolean existsByCategoryId(Long categoryId);

  /**
   * Recherche plein texte Postgres (to_tsvector/websearch_to_tsquery, index GIN — voir
   * V7__add_product_fulltext_search_index.sql), triée par pertinence. Les autres filtres
   * (catégorie, marque, prix, stock) sont exprimés en SQL "optionnel" (`:param IS NULL OR ...`)
   * pour rester composables avec la recherche texte sans dupliquer la logique de {@link
   * com.shop.catalog.spec.ProductSpecifications}, qui elle reste utilisée quand aucun texte n'est
   * recherché (cas largement majoritaire, pas de raison de payer le coût d'une requête native à
   * chaque fois).
   */
  @Query(
      value =
          "SELECT p.* FROM products p "
              + "JOIN categories c ON p.category_id = c.id "
              + "WHERE (:active IS NULL OR p.active = :active) "
              + "AND (:category IS NULL OR c.slug = :category) "
              + "AND (:brand IS NULL OR lower(p.brand) = lower(:brand)) "
              + "AND ((:minPrice IS NULL AND :maxPrice IS NULL) OR EXISTS ("
              + "     SELECT 1 FROM product_variants v WHERE v.product_id = p.id "
              + "     AND (:minPrice IS NULL OR v.price >= :minPrice) "
              + "     AND (:maxPrice IS NULL OR v.price <= :maxPrice))) "
              + "AND (:inStock IS NULL OR :inStock = false OR EXISTS ("
              + "     SELECT 1 FROM product_variants v WHERE v.product_id = p.id AND v.active = true AND v.stock > 0)) "
              + "AND to_tsvector('french', coalesce(p.name,'') || ' ' || coalesce(p.brand,'') || ' ' || coalesce(p.description,'')) "
              + "    @@ websearch_to_tsquery('french', :q) "
              + "ORDER BY ts_rank(to_tsvector('french', coalesce(p.name,'') || ' ' || coalesce(p.brand,'') || ' ' || coalesce(p.description,'')), websearch_to_tsquery('french', :q)) DESC",
      countQuery =
          "SELECT count(*) FROM products p "
              + "JOIN categories c ON p.category_id = c.id "
              + "WHERE (:active IS NULL OR p.active = :active) "
              + "AND (:category IS NULL OR c.slug = :category) "
              + "AND (:brand IS NULL OR lower(p.brand) = lower(:brand)) "
              + "AND ((:minPrice IS NULL AND :maxPrice IS NULL) OR EXISTS ("
              + "     SELECT 1 FROM product_variants v WHERE v.product_id = p.id "
              + "     AND (:minPrice IS NULL OR v.price >= :minPrice) "
              + "     AND (:maxPrice IS NULL OR v.price <= :maxPrice))) "
              + "AND (:inStock IS NULL OR :inStock = false OR EXISTS ("
              + "     SELECT 1 FROM product_variants v WHERE v.product_id = p.id AND v.active = true AND v.stock > 0)) "
              + "AND to_tsvector('french', coalesce(p.name,'') || ' ' || coalesce(p.brand,'') || ' ' || coalesce(p.description,'')) "
              + "    @@ websearch_to_tsquery('french', :q)",
      nativeQuery = true)
  Page<Product> searchFullText(
      @Param("active") Boolean active,
      @Param("category") String category,
      @Param("brand") String brand,
      @Param("minPrice") BigDecimal minPrice,
      @Param("maxPrice") BigDecimal maxPrice,
      @Param("inStock") Boolean inStock,
      @Param("q") String q,
      Pageable pageable);
}
