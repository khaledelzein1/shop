package com.shop.catalog.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Map;

public record ProductVariantRequest(
    @NotBlank String sku,
    @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal price,
    @NotNull @Min(0) Integer stock,
    boolean active,

    /** Attributs libres selon la catégorie : taille/couleur, ram/stockage... */
    Map<String, String> attributes) {}
