package com.shop.cart.dto;

import com.shop.cart.Cart;
import java.math.BigDecimal;
import java.util.List;

public record CartResponse(Long id, List<CartItemResponse> items, BigDecimal totalAmount) {

  public static CartResponse from(Cart cart) {
    List<CartItemResponse> items = cart.getItems().stream().map(CartItemResponse::from).toList();
    BigDecimal total =
        items.stream().map(CartItemResponse::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    return new CartResponse(cart.getId(), items, total);
  }

  public static CartResponse empty() {
    return new CartResponse(null, List.of(), BigDecimal.ZERO);
  }
}
