package com.shop.cart;

import com.shop.cart.dto.AddCartItemRequest;
import com.shop.cart.dto.CartResponse;
import com.shop.cart.dto.UpdateCartItemRequest;
import com.shop.catalog.ProductVariant;
import com.shop.catalog.ProductVariantRepository;
import com.shop.common.exception.ConflictException;
import com.shop.common.exception.ResourceNotFoundException;
import com.shop.user.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartService {

  private final CartRepository cartRepository;
  private final CartItemRepository cartItemRepository;
  private final UserRepository userRepository;
  private final ProductVariantRepository variantRepository;

  @Transactional(readOnly = true)
  public CartResponse getCart(Long userId) {
    return cartRepository
        .findByUserId(userId)
        .map(CartResponse::from)
        .orElseGet(CartResponse::empty);
  }

  @Transactional
  public CartResponse addItem(Long userId, AddCartItemRequest request) {
    Cart cart = getOrCreateCart(userId);
    ProductVariant variant =
        variantRepository
            .findById(request.variantId())
            .filter(ProductVariant::isActive)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Variante introuvable ou indisponible (id=" + request.variantId() + ")"));

    Optional<CartItem> existing =
        cartItemRepository.findByCartIdAndVariantId(cart.getId(), variant.getId());
    int newQuantity = existing.map(CartItem::getQuantity).orElse(0) + request.quantity();
    ensureStockAvailable(variant, newQuantity);

    if (existing.isPresent()) {
      CartItem item = existing.get();
      item.setQuantity(newQuantity);
      item.setUnitPriceSnapshot(variant.getPrice());
    } else {
      CartItem item = new CartItem();
      item.setCart(cart);
      item.setVariant(variant);
      item.setQuantity(request.quantity());
      item.setUnitPriceSnapshot(variant.getPrice());
      cart.getItems().add(item);
      cartItemRepository.save(item);
    }

    return CartResponse.from(cart);
  }

  @Transactional
  public CartResponse updateItemQuantity(Long userId, Long itemId, UpdateCartItemRequest request) {
    Cart cart = getCartOrThrow(userId);
    CartItem item = findOwnedItemOrThrow(cart, itemId);
    ensureStockAvailable(item.getVariant(), request.quantity());
    item.setQuantity(request.quantity());
    return CartResponse.from(cart);
  }

  @Transactional
  public CartResponse removeItem(Long userId, Long itemId) {
    Cart cart = getCartOrThrow(userId);
    CartItem item = findOwnedItemOrThrow(cart, itemId);
    cartItemRepository.delete(item);
    return CartResponse.from(cart);
  }

  private void ensureStockAvailable(ProductVariant variant, int requestedQuantity) {
    if (requestedQuantity > variant.getStock()) {
      throw new ConflictException(
          "Stock insuffisant pour '"
              + variant.getSku()
              + "' ("
              + variant.getStock()
              + " disponible(s))");
    }
  }

  private Cart getOrCreateCart(Long userId) {
    return cartRepository
        .findByUserId(userId)
        .orElseGet(
            () -> {
              Cart cart = new Cart();
              cart.setUser(userRepository.getReferenceById(userId));
              return cartRepository.save(cart);
            });
  }

  private Cart getCartOrThrow(Long userId) {
    return cartRepository
        .findByUserId(userId)
        .orElseThrow(() -> new ResourceNotFoundException("Panier introuvable"));
  }

  private CartItem findOwnedItemOrThrow(Cart cart, Long itemId) {
    return cartItemRepository
        .findByIdAndCartId(itemId, cart.getId())
        .orElseThrow(
            () ->
                new ResourceNotFoundException("Article de panier introuvable (id=" + itemId + ")"));
  }
}
