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
import com.shop.order.dto.CheckoutResponse;
import com.shop.order.dto.OrderResponse;
import com.shop.order.dto.OrderSummaryResponse;
import com.shop.order.spec.OrderSpecifications;
import com.shop.payment.DemoCardProcessor;
import com.shop.payment.DemoCardProcessor.ApprovedPayment;
import com.shop.payment.PaymentDeclinedException;
import com.shop.payment.PaymentGateway;
import com.shop.payment.PaymentGateway.PaymentSession;
import com.shop.payment.PaymentGateway.PaymentStatus;
import com.shop.payment.PaymentProvider;
import com.shop.payment.StripeProperties;
import com.shop.user.User;
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
 * Checkout avec paiement par carte (Stripe Checkout) :
 *
 * <ol>
 *   <li>{@link #checkout} crée la commande en PENDING, réserve le stock et ouvre une session de
 *       paiement Stripe. Le panier n'est pas encore vidé.
 *   <li>Le client paie sur la page Stripe puis revient : {@link #confirmPayment} relit le statut
 *       chez Stripe et, si c'est payé, passe la commande en CONFIRMED et vide le panier.
 *   <li>S'il abandonne ({@link #cancelPayment}) ou si la session expire ({@link #syncPayment},
 *       appelé par PaymentReconciliationJob), la commande est annulée et le stock restitué ; le
 *       panier, intact, permet de réessayer.
 * </ol>
 *
 * <p>Sans clé Stripe (mode DEMO), la carte saisie dans le formulaire intégré est contrôlée par
 * {@link DemoCardProcessor} et la commande est directement CONFIRMED — aucun débit réel.
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
  private final PaymentGateway paymentGateway;
  private final StripeProperties stripeProperties;
  private final DemoCardProcessor demoCardProcessor;

  @Transactional
  public CheckoutResponse checkout(Long userId, CheckoutRequest request) {
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

    // Mode DEMO (pas de clé Stripe) : la carte est contrôlée avant toute écriture, donc un refus
    // ne crée pas de commande et ne touche pas au stock.
    ApprovedPayment demoPayment = null;
    if (PaymentProvider.of(stripeProperties) == PaymentProvider.DEMO) {
      if (request.card() == null) {
        throw new PaymentDeclinedException("Please enter your card details.");
      }
      demoPayment = demoCardProcessor.charge(request.card());
    }

    User user = userRepository.getReferenceById(userId);
    Order order = new Order();
    order.setUser(user);
    order.setStatus(OrderStatus.PENDING);
    order.setShippingLabel(address.getLabel());
    order.setShippingStreet(address.getStreet());
    order.setShippingCity(address.getCity());
    order.setShippingZipCode(address.getZipCode());
    order.setShippingCountry(address.getCountry());

    BigDecimal total = BigDecimal.ZERO;
    for (CartItem cartItem : cart.getItems()) {
      ProductVariant variant = cartItem.getVariant();
      // Réservation : le stock est restitué si le paiement n'aboutit pas.
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

    if (demoPayment != null) {
      saved.setCardBrand(demoPayment.cardBrand());
      saved.setCardLast4(demoPayment.cardLast4());
      markPaid(saved);
      return new CheckoutResponse(OrderResponse.from(saved), null);
    }

    // Si Stripe échoue, l'exception annule toute la transaction (commande + réservation de stock).
    PaymentSession session = paymentGateway.createSession(saved, user.getEmail());
    saved.setStripeSessionId(session.id());

    return new CheckoutResponse(OrderResponse.from(saved), session.url());
  }

  /**
   * Appelé au retour de la page Stripe. Ne fait pas confiance au navigateur : le statut est relu
   * chez Stripe. Renvoie la commande telle qu'elle est après vérification (CONFIRMED si payée,
   * encore PENDING si Stripe n'a pas fini de traiter le paiement).
   */
  @Transactional
  public OrderResponse confirmPayment(Long userId, Long orderId) {
    Order order = findForUserOrThrow(userId, orderId);
    syncPayment(order);
    return OrderResponse.from(order);
  }

  /**
   * Le client a quitté la page Stripe sans payer : la session est fermée pour qu'elle ne puisse
   * plus être payée, puis la commande est annulée (stock restitué). Si le paiement est passé
   * entre-temps, la commande est confirmée à la place.
   */
  @Transactional
  public OrderResponse cancelPayment(Long userId, Long orderId) {
    Order order = findForUserOrThrow(userId, orderId);
    if (order.getStatus() == OrderStatus.PENDING && order.getStripeSessionId() != null) {
      paymentGateway.expireSession(order.getStripeSessionId());
      if (paymentGateway.getStatus(order.getStripeSessionId()) == PaymentStatus.PAID) {
        markPaid(order);
      } else {
        cancel(order);
      }
    }
    return OrderResponse.from(order);
  }

  /** Version utilisée par PaymentReconciliationJob, hors contexte utilisateur. */
  @Transactional
  public void syncPayment(Long orderId) {
    syncPayment(findByIdOrThrow(orderId));
  }

  /** Aligne une commande PENDING sur le statut de son paiement chez Stripe. */
  private void syncPayment(Order order) {
    if (order.getStatus() != OrderStatus.PENDING || order.getStripeSessionId() == null) {
      return;
    }
    switch (paymentGateway.getStatus(order.getStripeSessionId())) {
      case PAID -> markPaid(order);
      case EXPIRED -> cancel(order);
      case OPEN -> {
        // Le client peut encore payer : rien à faire.
      }
    }
  }

  /** Paiement reçu : commande confirmée, et les articles achetés sont retirés du panier. */
  private void markPaid(Order order) {
    order.setStatus(OrderStatus.CONFIRMED);
    cartRepository
        .findByUserId(order.getUser().getId())
        .ifPresent(
            cart -> {
              // On ne retire que ce qui a été commandé : le client a pu ajouter d'autres
              // articles au panier pendant le paiement.
              for (OrderItem item : order.getItems()) {
                if (item.getVariant() == null) {
                  continue;
                }
                Long variantId = item.getVariant().getId();
                cart.getItems().stream()
                    .filter(c -> c.getVariant().getId().equals(variantId))
                    .findFirst()
                    .ifPresent(c -> c.setQuantity(c.getQuantity() - item.getQuantity()));
              }
              cart.getItems().removeIf(c -> c.getQuantity() <= 0);
            });
  }

  private void cancel(Order order) {
    restoreStock(order);
    order.setStatus(OrderStatus.CANCELLED);
  }

  private void restoreStock(Order order) {
    for (OrderItem item : order.getItems()) {
      ProductVariant variant = item.getVariant();
      if (variant != null) {
        variant.setStock(variant.getStock() + item.getQuantity());
      }
    }
  }

  @Transactional(readOnly = true)
  public PageResponse<OrderSummaryResponse> history(Long userId, Pageable pageable) {
    return PageResponse.from(
        orderRepository.findByUserId(userId, pageable).map(OrderSummaryResponse::from));
  }

  @Transactional(readOnly = true)
  public OrderResponse getOrderForUser(Long userId, Long orderId) {
    return OrderResponse.from(findForUserOrThrow(userId, orderId));
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
      restoreStock(order);
    }

    order.setStatus(newStatus);
    return OrderResponse.from(order);
  }

  private Order findForUserOrThrow(Long userId, Long orderId) {
    return orderRepository
        .findByIdAndUserId(orderId, userId)
        .orElseThrow(() -> new ResourceNotFoundException("Order not found (id=" + orderId + ")"));
  }

  private Order findByIdOrThrow(Long orderId) {
    return orderRepository
        .findById(orderId)
        .orElseThrow(() -> new ResourceNotFoundException("Order not found (id=" + orderId + ")"));
  }
}
