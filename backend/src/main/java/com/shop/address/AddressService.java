package com.shop.address;

import com.shop.address.dto.AddressRequest;
import com.shop.address.dto.AddressResponse;
import com.shop.common.exception.ResourceNotFoundException;
import com.shop.user.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddressService {

  private final AddressRepository addressRepository;
  private final UserRepository userRepository;

  @Transactional(readOnly = true)
  public List<AddressResponse> list(Long userId) {
    return addressRepository.findByUserIdOrderByDefaultAddressDescCreatedAtAsc(userId).stream()
        .map(AddressResponse::from)
        .toList();
  }

  @Transactional
  public AddressResponse create(Long userId, AddressRequest request) {
    if (request.defaultAddress()) {
      addressRepository.clearDefaultForUser(userId);
    }
    Address address = new Address();
    applyRequest(address, request);
    address.setUser(userRepository.getReferenceById(userId));
    return AddressResponse.from(addressRepository.save(address));
  }

  @Transactional
  public AddressResponse update(Long userId, Long addressId, AddressRequest request) {
    Address address = findOwnedOrThrow(userId, addressId);
    if (request.defaultAddress()) {
      addressRepository.clearDefaultForUser(userId);
    }
    applyRequest(address, request);
    return AddressResponse.from(address);
  }

  @Transactional
  public void delete(Long userId, Long addressId) {
    Address address = findOwnedOrThrow(userId, addressId);
    addressRepository.delete(address);
  }

  private void applyRequest(Address address, AddressRequest request) {
    address.setLabel(request.label());
    address.setStreet(request.street());
    address.setCity(request.city());
    address.setZipCode(request.zipCode());
    address.setCountry(request.country());
    address.setDefaultAddress(request.defaultAddress());
  }

  private Address findOwnedOrThrow(Long userId, Long addressId) {
    return addressRepository
        .findByIdAndUserId(addressId, userId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Address not found (id=" + addressId + ")"));
  }
}
