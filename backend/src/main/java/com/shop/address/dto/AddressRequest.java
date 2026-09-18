package com.shop.address.dto;

import jakarta.validation.constraints.NotBlank;

public record AddressRequest(
    String label,
    @NotBlank String street,
    @NotBlank String city,
    @NotBlank String zipCode,
    @NotBlank String country,
    boolean defaultAddress) {}
