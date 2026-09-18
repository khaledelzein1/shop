package com.shop.order.dto;

import com.shop.order.Order;
import com.shop.order.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;

public record AdminOrderSummaryResponse(
        Long id,
        OrderStatus status,
        BigDecimal totalAmount,
        Instant createdAt,
        int itemCount,
        String userEmail) {

    public static AdminOrderSummaryResponse from(Order order) {
        return new AdminOrderSummaryResponse(
                order.getId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                order.getItems().size(),
                order.getUser().getEmail());
    }
}
