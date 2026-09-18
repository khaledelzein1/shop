package com.shop.catalog.dto;

import jakarta.validation.constraints.NotBlank;

public record ProductImageRequest(@NotBlank String url, int position, boolean primary) {}
