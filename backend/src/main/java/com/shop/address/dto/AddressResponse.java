package com.shop.address.dto;

import com.shop.address.Address;

public record AddressResponse(
    Long id,
    String label,
    String street,
    String city,
    String zipCode,
    String country,
    boolean defaultAddress) {

  public static AddressResponse from(Address address) {
    return new AddressResponse(
        address.getId(),
        address.getLabel(),
        address.getStreet(),
        address.getCity(),
        address.getZipCode(),
        address.getCountry(),
        address.isDefaultAddress());
  }
}
