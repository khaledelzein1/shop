package com.shop.catalog;

import com.shop.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Une déclinaison achetable d'un {@link Product} (ex. taille/couleur pour un vêtement, RAM/stockage
 * pour de l'informatique). Les attributs propres à la catégorie sont stockés en JSONB plutôt que
 * normalisés (voir docs/ARCHITECTURE.md §5) pour rester flexible entre familles de produits très
 * différentes.
 */
@Entity
@Table(name = "product_variants")
@Getter
@Setter
@NoArgsConstructor
public class ProductVariant extends BaseEntity {

  @Column(nullable = false, unique = true)
  private String sku;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal price;

  @Column(nullable = false)
  private int stock;

  @Column(nullable = false)
  private boolean active = true;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(nullable = false, columnDefinition = "jsonb")
  private Map<String, String> attributes = new HashMap<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;
}
