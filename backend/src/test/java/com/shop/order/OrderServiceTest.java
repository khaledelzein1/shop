package com.shop.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.shop.address.AddressRepository;
import com.shop.cart.Cart;
import com.shop.cart.CartRepository;
import com.shop.catalog.ProductVariant;
import com.shop.common.exception.ConflictException;
import com.shop.order.dto.CheckoutRequest;
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
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void checkout_withEmptyCart_throwsConflictException() {
        Cart cart = new Cart();
        ReflectionTestUtils.setField(cart, "id", 1L);
        when(cartRepository.findByUserId(10L)).thenReturn(Optional.of(cart));

        assertThatThrownBy(() -> orderService.checkout(10L, new CheckoutRequest(1L)))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("panier est vide");
    }

    @Test
    void updateStatus_invalidTransition_throwsConflictException() {
        Order order = new Order();
        ReflectionTestUtils.setField(order, "id", 1L);
        order.setStatus(OrderStatus.DELIVERED);
        order.setTotalAmount(BigDecimal.TEN);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.updateStatus(1L, OrderStatus.PENDING))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Transition de statut invalide");
    }

    @Test
    void updateStatus_toCancelled_restoresVariantStock() {
        ProductVariant variant = new ProductVariant();
        ReflectionTestUtils.setField(variant, "id", 1L);
        variant.setStock(3);

        OrderItem item = new OrderItem();
        ReflectionTestUtils.setField(item, "id", 1L);
        item.setVariant(variant);
        item.setQuantity(2);
        item.setUnitPrice(BigDecimal.TEN);

        Order order = new Order();
        ReflectionTestUtils.setField(order, "id", 1L);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(BigDecimal.TEN);
        order.getItems().add(item);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.updateStatus(1L, OrderStatus.CANCELLED);

        assertThat(variant.getStock()).isEqualTo(5);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }
}
