package com.shop.order;

import com.shop.common.dto.PageResponse;
import com.shop.order.dto.AdminOrderSummaryResponse;
import com.shop.order.dto.OrderResponse;
import com.shop.order.dto.UpdateOrderStatusRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class OrderAdminController {

    private final OrderService orderService;

    @GetMapping
    public PageResponse<AdminOrderSummaryResponse> search(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) Long userId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return orderService.searchAdmin(status, userId, pageable);
    }

    @GetMapping("/{id}")
    public OrderResponse getById(@PathVariable Long id) {
        return orderService.getByIdForAdmin(id);
    }

    @PatchMapping("/{id}/status")
    public OrderResponse updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateOrderStatusRequest request) {
        return orderService.updateStatus(id, request.status());
    }
}
