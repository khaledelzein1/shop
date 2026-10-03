package com.shop.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CategoryRequest(
    @NotBlank String name,
    @NotBlank
        @Pattern(
            regexp = "^[a-z0-9]+(-[a-z0-9]+)*$",
            message = "Slug must be lowercase, with no spaces (e.g. hardware-computers)")
        String slug,
    String description) {}
