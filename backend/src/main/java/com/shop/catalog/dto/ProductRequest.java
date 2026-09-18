package com.shop.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Champs produit "de base". Les variantes et images sont gérées via leurs propres endpoints (POST
 * /api/admin/products/{id}/variants|images) plutôt qu'imbriquées ici, pour éviter une logique de
 * diff complexe sur les collections lors d'un update.
 */
public record ProductRequest(
    @NotBlank String name,
    @NotBlank
        @Pattern(
            regexp = "^[a-z0-9]+(-[a-z0-9]+)*$",
            message = "Le slug doit être en minuscules, sans espaces (ex: laptop-pro-15)")
        String slug,
    String description,
    String brand,
    boolean active,
    @NotNull Long categoryId) {}
