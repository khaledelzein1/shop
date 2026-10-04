package com.shop.order;

import com.shop.common.dto.PageResponse;
import com.shop.order.dto.CheckoutRequest;
import com.shop.order.dto.CheckoutResponse;
import com.shop.order.dto.OrderResponse;
import com.shop.order.dto.OrderSummaryResponse;
import com.shop.security.config.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me/orders")
@RequiredArgsConstructor
@Tag(name = "Commandes", description = "Checkout et historique de l'utilisateur connecté")
public class OrderController {

  private final OrderService orderService;

  @PostMapping("/checkout")
  @Operation(
      summary = "Passer commande et ouvrir le paiement par carte",
      description =
          "Convertit le panier en commande PENDING (stock réservé, produits/adresse "
              + "snapshottés) et ouvre une session Stripe Checkout. Le client doit être redirigé "
              + "vers paymentUrl. 503 si le paiement par carte n'est pas configuré.")
  public ResponseEntity<CheckoutResponse> checkout(
      @AuthenticationPrincipal UserPrincipal principal,
      @Valid @RequestBody CheckoutRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(orderService.checkout(principal.getId(), request));
  }

  @PostMapping("/{id}/payment/confirm")
  @Operation(
      summary = "Vérifier le paiement au retour de Stripe",
      description =
          "Relit le statut du paiement chez Stripe : si la commande est payée elle passe "
              + "CONFIRMED et les articles achetés sont retirés du panier.")
  public OrderResponse confirmPayment(
      @AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
    return orderService.confirmPayment(principal.getId(), id);
  }

  @PostMapping("/{id}/payment/cancel")
  @Operation(
      summary = "Abandonner le paiement",
      description =
          "Ferme la session Stripe et annule la commande PENDING (stock restitué). Le panier "
              + "est conservé pour pouvoir réessayer.")
  public OrderResponse cancelPayment(
      @AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
    return orderService.cancelPayment(principal.getId(), id);
  }

  @GetMapping
  public PageResponse<OrderSummaryResponse> history(
      @AuthenticationPrincipal UserPrincipal principal,
      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {
    return orderService.history(principal.getId(), pageable);
  }

  @GetMapping("/{id}")
  public OrderResponse getOrder(
      @AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
    return orderService.getOrderForUser(principal.getId(), id);
  }
}
