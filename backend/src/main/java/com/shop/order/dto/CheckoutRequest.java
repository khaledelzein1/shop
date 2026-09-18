package com.shop.order.dto;

import jakarta.validation.constraints.NotNull;

public record CheckoutRequest(@NotNull Long addressId) {
}
