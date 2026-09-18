package com.shop.catalog.dto;

import java.math.BigDecimal;

/** Regroupe les paramètres de recherche/filtrage du catalogue produits. */
public record ProductFilter(
    String category,
    String brand,
    BigDecimal minPrice,
    BigDecimal maxPrice,
    Boolean inStock,
    String q) {}
