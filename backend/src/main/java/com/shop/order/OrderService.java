package com.shop.order;

import com.shop.address.Address;
import com.shop.address.AddressRepository;
import com.shop.cart.Cart;
import com.shop.cart.CartItem;
import com.shop.cart.CartRepository;
import com.shop.catalog.ProductVariant;
import com.shop.common.dto.PageResponse;
import com.shop.common.exception.ConflictException;
import com.shop.common.exception.ResourceNotFoundException;
import com.shop.order.dto.AdminOrderSummaryResponse;
import com.shop.order.dto.CheckoutRequest;
import com.shop.order.dto.OrderResponse;
import com.shop.order.dto.OrderSummaryResponse;
import com.shop.order.spec.OrderSpecifications;
import com.shop.user.UserRepository;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Checkout "simulé" : pas de vraie passerelle de paiement, mais le stock est réellement vérifié et
 * décrémenté, et la commande créée devient tout de suite CONFIRMED (pas d'étape PENDING qui
 * n'aurait de sens qu'en attente d'une confirmation de paiement réelle — voir
 * docs/ARCHITECTURE.md).
 */
@Service
@RequiredArgsConstructor
public class OrderService {

  /**
   * Transitions de statut autorisées — évite qu'un admin puisse renvoyer une commande livrée à "en
   * attente" ou d'autres changements incohérents. DELIVERED et CANCELLED sont des états terminaux.
   */
  private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS =
      Map.of(
          OrderStatus.PENDING, Set.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED),
          OrderStatus.CONFIRMED, Set.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED),
          OrderStatus.SHIPPED, Set.of(OrderStatus.DELIVERED),
          OrderStatus.DELIVERED, Set.of(),
          OrderStatus.CANCELLED, Set.of());

  private final OrderRepository orderRepository;
  private final CartRepository cartRepository;
  private final AddressRepository addressRepository;
  private final UserRepository userRepository;

  @Transactional
  public OrderResponse checkout(Long userId, CheckoutRequest request) {
    Cart cart =
        cartRepository
            .findByUserId(userId)
            .filter(c -> !c.getItems().isEmpty())
            .orElseThrow(() -> new ConflictException("Cart is empty"));

    Address address =
        addressRepository
            .findByIdAndUserId(request.addressId(), userId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Address not found (id=" + request.addressId() + ")"));

    for (CartItem cartItem : cart.getItems()) {
      ProductVariant variant = cartItem.getVariant();
      if (!variant.isActive() || variant.getStock() < cartItem.getQuantity()) {
        throw new ConflictException(
            "Insufficient stock for '"
                + variant.getSku()
                + "' — please check your cart before ordering");
      }
    }

    Order order = new Order();
    order.setUser(userRepository.getReferenceById(userId));
    order.setStatus(OrderStatus.CONFIRMED);
    order.setShippingLabel(address.getLabel());
    order.setShippingStreet(address.getStreet());
    order.setShippingCity(address.getCity());
    order.setShippingZipCode(address.getZipCode());
    order.setShippingCountry(address.getCountry());

    BigDecimal total = BigDecimal.ZERO;
    for (CartItem cartItem : cart.getItems()) {
      ProductVariant variant = cartItem.getVariant();
      variant.setStock(variant.getStock() - cartItem.getQuantity());

      OrderItem orderItem = new OrderItem();
      orderItem.setOrder(order);
      orderItem.setVariant(variant);
      orderItem.setProductName(variant.getProduct().getName());
      orderItem.setSku(variant.getSku());
      orderItem.setQuantity(cartItem.getQuantity());
      orderItem.setUnitPrice(cartItem.getUnitPriceSnapshot());
      order.getItems().add(orderItem);

      total =
          total.add(
              cartItem.getUnitPriceSnapshot().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
    }
    order.setTotalAmount(total);

    Order saved = orderRepository.save(order);
    cart.getItems().clear();

    return OrderResponse.from(saved);
  }

  @Transactional(readOnly = true)
  public PageResponse<OrderSummaryResponse> history(Long userId, Pageable pageable) {
    return PageResponse.from(
        orderRepository.findByUserId(userId, pageable).map(OrderSummaryResponse::from));
  }

  @Transactional(readOnly = true)
  public OrderResponse getOrderForUser(Long userId, Long orderId) {
    Order order =
        orderRepository
            .findByIdAndUserId(orderId, userId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Order not found (id=" + orderId + ")"));
    return OrderResponse.from(order);
  }

  @Transactional(readOnly = true)
  public PageResponse<AdminOrderSummaryResponse> searchAdmin(
      OrderStatus status, Long userId, Pageable pageable) {
    Specification<Order> spec =
        Specification.where(OrderSpecifications.hasStatus(status))
            .and(OrderSpecifications.hasUserId(userId));
    return PageResponse.from(
        orderRepository.findAll(spec, pageable).map(AdminOrderSummaryResponse::from));
  }

  @Transactional(readOnly = true)
  public OrderResponse getByIdForAdmin(Long orderId) {
    Order order = findByIdOrThrow(orderId);
    return OrderResponse.from(order);
  }

  /** Le repassage à CANCELLED restitue le stock des variantes concernées. */
  @Transactional
  public OrderResponse updateStatus(Long orderId, OrderStatus newStatus) {
    Order order = findByIdOrThrow(orderId);
    if (order.getStatus() == newStatus) {
      return OrderResponse.from(order);
    }
    Set<OrderStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(order.getStatus(), Set.of());
    if (!allowed.contains(newStatus)) {
      throw new ConflictException(
          "Invalid status transition: " + order.getStatus() + " -> " + newStatus);
    }

    if (newStatus == OrderStatus.CANCELLED) {
      for (OrderItem item : order.getItems()) {
        ProductVariant variant = item.getVariant();
        if (variant != null) {
          variant.setStock(variant.getStock() + item.getQuantity());
        }
      }
    }

    order.setStatus(newStatus);
    return OrderResponse.from(order);
  }

  private Order findByIdOrThrow(Long orderId) {
    return orderRepository
        .findById(orderId)
        .orElseThrow(() -> new ResourceNotFoundException("Order not found (id=" + orderId + ")"));
  }
}
