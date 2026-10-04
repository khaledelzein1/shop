package com.shop.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shop.address.AddressRepository;
import com.shop.cart.Cart;
import com.shop.cart.CartItem;
import com.shop.cart.CartRepository;
import com.shop.catalog.ProductVariant;
import com.shop.common.exception.ConflictException;
import com.shop.order.dto.CheckoutRequest;
import com.shop.payment.DemoCardProcessor;
import com.shop.payment.PaymentGateway;
import com.shop.payment.PaymentGateway.PaymentStatus;
import com.shop.payment.StripeProperties;
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
class OrderServiceTest {

  @Mock private OrderRepository orderRepository;

  @Mock private CartRepository cartRepository;

  @Mock private AddressRepository addressRepository;

  @Mock private UserRepository userRepository;

  @Mock private PaymentGateway paymentGateway;

  @Mock private StripeProperties stripeProperties;

  @Mock private DemoCardProcessor demoCardProcessor;

  @InjectMocks private OrderService orderService;

  @Test
  void checkout_withEmptyCart_throwsConflictException() {
    Cart cart = new Cart();
    ReflectionTestUtils.setField(cart, "id", 1L);
    when(cartRepository.findByUserId(10L)).thenReturn(Optional.of(cart));

    assertThatThrownBy(() -> orderService.checkout(10L, new CheckoutRequest(1L)))
        .isInstanceOf(ConflictException.class)
        .hasMessageContaining("Cart is empty");
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
        .hasMessageContaining("Invalid status transition");
  }

  @Test
  void updateStatus_toCancelled_restoresVariantStock() {
    ProductVariant variant = variant(1L, 3);
    Order order = pendingOrder(variant, 2);
    order.setStatus(OrderStatus.CONFIRMED);

    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

    orderService.updateStatus(1L, OrderStatus.CANCELLED);

    assertThat(variant.getStock()).isEqualTo(5);
    assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
  }

  @Test
  void confirmPayment_whenStripeSaysPaid_confirmsOrderAndRemovesBoughtItemsFromCart() {
    ProductVariant bought = variant(1L, 8);
    ProductVariant addedDuringPayment = variant(2L, 5);
    Order order = pendingOrder(bought, 2);
    Cart cart = new Cart();
    cart.getItems().add(cartItem(cart, bought, 2));
    cart.getItems().add(cartItem(cart, addedDuringPayment, 1));

    when(orderRepository.findByIdAndUserId(1L, 10L)).thenReturn(Optional.of(order));
    when(paymentGateway.getStatus("cs_test_1")).thenReturn(PaymentStatus.PAID);
    when(cartRepository.findByUserId(10L)).thenReturn(Optional.of(cart));

    orderService.confirmPayment(10L, 1L);

    assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    assertThat(cart.getItems())
        .singleElement()
        .satisfies(item -> assertThat(item.getVariant()).isSameAs(addedDuringPayment));
    assertThat(bought.getStock()).isEqualTo(8);
  }

  @Test
  void confirmPayment_whenSessionStillOpen_leavesOrderPending() {
    ProductVariant variant = variant(1L, 8);
    Order order = pendingOrder(variant, 2);

    when(orderRepository.findByIdAndUserId(1L, 10L)).thenReturn(Optional.of(order));
    when(paymentGateway.getStatus("cs_test_1")).thenReturn(PaymentStatus.OPEN);

    orderService.confirmPayment(10L, 1L);

    assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
    verify(cartRepository, never()).findByUserId(10L);
  }

  @Test
  void cancelPayment_expiresSessionCancelsOrderAndRestoresStock() {
    ProductVariant variant = variant(1L, 8);
    Order order = pendingOrder(variant, 2);

    when(orderRepository.findByIdAndUserId(1L, 10L)).thenReturn(Optional.of(order));
    when(paymentGateway.getStatus("cs_test_1")).thenReturn(PaymentStatus.EXPIRED);

    orderService.cancelPayment(10L, 1L);

    verify(paymentGateway).expireSession("cs_test_1");
    assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    assertThat(variant.getStock()).isEqualTo(10);
  }

  @Test
  void cancelPayment_whenPaidMeanwhile_confirmsInsteadOfCancelling() {
    ProductVariant variant = variant(1L, 8);
    Order order = pendingOrder(variant, 2);

    when(orderRepository.findByIdAndUserId(1L, 10L)).thenReturn(Optional.of(order));
    when(paymentGateway.getStatus("cs_test_1")).thenReturn(PaymentStatus.PAID);
    when(cartRepository.findByUserId(10L)).thenReturn(Optional.empty());

    orderService.cancelPayment(10L, 1L);

    assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    assertThat(variant.getStock()).isEqualTo(8);
  }

  private static ProductVariant variant(Long id, int stock) {
    ProductVariant variant = new ProductVariant();
    ReflectionTestUtils.setField(variant, "id", id);
    variant.setStock(stock);
    return variant;
  }

  /** Commande PENDING de l'utilisateur 10 pour {@code quantity} × {@code variant}. */
  private static Order pendingOrder(ProductVariant variant, int quantity) {
    User user = new User();
    ReflectionTestUtils.setField(user, "id", 10L);

    OrderItem item = new OrderItem();
    ReflectionTestUtils.setField(item, "id", 1L);
    item.setVariant(variant);
    item.setQuantity(quantity);
    item.setUnitPrice(BigDecimal.TEN);

    Order order = new Order();
    ReflectionTestUtils.setField(order, "id", 1L);
    order.setUser(user);
    order.setStatus(OrderStatus.PENDING);
    order.setStripeSessionId("cs_test_1");
    order.setTotalAmount(BigDecimal.TEN);
    order.getItems().add(item);
    return order;
  }

  private static CartItem cartItem(Cart cart, ProductVariant variant, int quantity) {
    CartItem item = new CartItem();
    item.setCart(cart);
    item.setVariant(variant);
    item.setQuantity(quantity);
    item.setUnitPriceSnapshot(BigDecimal.TEN);
    return item;
  }
}
