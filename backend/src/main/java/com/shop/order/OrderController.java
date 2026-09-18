package com.shop.order;

import com.shop.common.dto.PageResponse;
import com.shop.order.dto.CheckoutRequest;
import com.shop.order.dto.OrderResponse;
import com.shop.order.dto.OrderSummaryResponse;
import com.shop.security.config.UserPrincipal;
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
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(
            @AuthenticationPrincipal UserPrincipal principal, @Valid @RequestBody CheckoutRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.checkout(principal.getId(), request));
    }

    @GetMapping
    public PageResponse<OrderSummaryResponse> history(
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return orderService.history(principal.getId(), pageable);
    }

    @GetMapping("/{id}")
    public OrderResponse getOrder(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        return orderService.getOrderForUser(principal.getId(), id);
    }
}
