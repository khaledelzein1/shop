package com.shop.cart;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.shop.cart.dto.AddCartItemRequest;
import com.shop.cart.dto.CartResponse;
import com.shop.catalog.Product;
import com.shop.catalog.ProductVariant;
import com.shop.catalog.ProductVariantRepository;
import com.shop.common.exception.ConflictException;
import com.shop.user.User;
import com.shop.user.UserRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

  @Mock private CartRepository cartRepository;

  @Mock private CartItemRepository cartItemRepository;

  @Mock private UserRepository userRepository;

  @Mock private ProductVariantRepository variantRepository;

  @InjectMocks private CartService cartService;

  private ProductVariant variantWithStock(int stock) {
    Product product = new Product();
    product.setName("Laptop Pro 15");
    product.setSlug("laptop-pro-15");

    ProductVariant variant = new ProductVariant();
    ReflectionTestUtils.setField(variant, "id", 1L);
    variant.setSku("LP15-8-256");
    variant.setPrice(new BigDecimal("899.99"));
    variant.setStock(stock);
    variant.setActive(true);
    variant.setProduct(product);
    return variant;
  }

  private Cart cartForUser(long userId) {
    Cart cart = new Cart();
    ReflectionTestUtils.setField(cart, "id", 1L);
    User user = new User();
    ReflectionTestUtils.setField(user, "id", userId);
    cart.setUser(user);
    return cart;
  }

  @Test
  void addItem_whenRequestedQuantityExceedsStock_throwsConflictException() {
    Cart cart = cartForUser(10L);

    when(cartRepository.findByUserId(10L)).thenReturn(Optional.of(cart));
    when(variantRepository.findById(1L)).thenReturn(Optional.of(variantWithStock(5)));
    when(cartItemRepository.findByCartIdAndVariantId(1L, 1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> cartService.addItem(10L, new AddCartItemRequest(1L, 10)))
        .isInstanceOf(ConflictException.class)
        .hasMessageContaining("Stock insuffisant");
  }

  @Test
  void addItem_whenSameVariantAlreadyInCart_mergesQuantityInsteadOfDuplicating() {
    Cart cart = cartForUser(10L);
    ProductVariant variant = variantWithStock(20);

    CartItem existingItem = new CartItem();
    ReflectionTestUtils.setField(existingItem, "id", 100L);
    existingItem.setCart(cart);
    existingItem.setVariant(variant);
    existingItem.setQuantity(2);
    existingItem.setUnitPriceSnapshot(variant.getPrice());
    cart.getItems().add(existingItem);

    when(cartRepository.findByUserId(10L)).thenReturn(Optional.of(cart));
    when(variantRepository.findById(1L)).thenReturn(Optional.of(variant));
    when(cartItemRepository.findByCartIdAndVariantId(1L, 1L)).thenReturn(Optional.of(existingItem));

    CartResponse response = cartService.addItem(10L, new AddCartItemRequest(1L, 3));

    assertThat(existingItem.getQuantity()).isEqualTo(5);
    assertThat(response.items()).hasSize(1);
    assertThat(response.items().get(0).quantity()).isEqualTo(5);
  }
}
