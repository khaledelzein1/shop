package com.shop.cart;

import com.shop.cart.dto.AddCartItemRequest;
import com.shop.cart.dto.CartResponse;
import com.shop.cart.dto.UpdateCartItemRequest;
import com.shop.security.config.UserPrincipal;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Panier de l'utilisateur connecté — créé à la volée au premier ajout. */
@RestController
@RequestMapping("/api/me/cart")
@RequiredArgsConstructor
@Tag(name = "Panier", description = "Panier de l'utilisateur connecté")
public class CartController {

  private final CartService cartService;

  @GetMapping
  public CartResponse getCart(@AuthenticationPrincipal UserPrincipal principal) {
    return cartService.getCart(principal.getId());
  }

  @PostMapping("/items")
  public ResponseEntity<CartResponse> addItem(
      @AuthenticationPrincipal UserPrincipal principal,
      @Valid @RequestBody AddCartItemRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(cartService.addItem(principal.getId(), request));
  }

  @PutMapping("/items/{itemId}")
  public CartResponse updateItem(
      @AuthenticationPrincipal UserPrincipal principal,
      @PathVariable Long itemId,
      @Valid @RequestBody UpdateCartItemRequest request) {
    return cartService.updateItemQuantity(principal.getId(), itemId, request);
  }

  @DeleteMapping("/items/{itemId}")
  public CartResponse removeItem(
      @AuthenticationPrincipal UserPrincipal principal, @PathVariable Long itemId) {
    return cartService.removeItem(principal.getId(), itemId);
  }
}
