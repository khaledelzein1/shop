package com.shop.order.dto;

import com.shop.order.OrderItem;
import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        String productName,
        String sku,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal) {

    public static OrderItemResponse from(OrderItem item) {
        BigDecimal lineTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        return new OrderItemResponse(
                item.getId(), item.getProductName(), item.getSku(), item.getQuantity(), item.getUnitPrice(), lineTotal);
    }
}
