package com.shop.order.dto;

import com.shop.order.Order;
import com.shop.order.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
    Long id,
    OrderStatus status,
    BigDecimal totalAmount,
    Instant createdAt,
    List<OrderItemResponse> items,
    ShippingAddressResponse shippingAddress,
    String cardBrand,
    String cardLast4) {

  public static OrderResponse from(Order order) {
    return new OrderResponse(
        order.getId(),
        order.getStatus(),
        order.getTotalAmount(),
        order.getCreatedAt(),
        order.getItems().stream().map(OrderItemResponse::from).toList(),
        ShippingAddressResponse.from(order),
        order.getCardBrand(),
        order.getCardLast4());
  }
}
