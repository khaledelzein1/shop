package com.shop.order.dto;

import com.shop.order.Order;

public record ShippingAddressResponse(
    String label, String street, String city, String zipCode, String country) {

  public static ShippingAddressResponse from(Order order) {
    return new ShippingAddressResponse(
        order.getShippingLabel(),
        order.getShippingStreet(),
        order.getShippingCity(),
        order.getShippingZipCode(),
        order.getShippingCountry());
  }
}
