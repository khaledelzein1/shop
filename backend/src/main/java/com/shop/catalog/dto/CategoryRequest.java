package com.shop.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CategoryRequest(
        @NotBlank String name,

        @NotBlank
        @Pattern(regexp = "^[a-z0-9]+(-[a-z0-9]+)*$", message = "Le slug doit être en minuscules, sans espaces (ex: materiel-informatique)")
        String slug,

        String description) {
}
