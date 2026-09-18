package com.shop.order.dto;

import com.shop.order.Order;
import com.shop.order.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;

public record OrderSummaryResponse(
    Long id, OrderStatus status, BigDecimal totalAmount, Instant createdAt, int itemCount) {

  public static OrderSummaryResponse from(Order order) {
    return new OrderSummaryResponse(
        order.getId(),
        order.getStatus(),
        order.getTotalAmount(),
        order.getCreatedAt(),
        order.getItems().size());
  }
}
