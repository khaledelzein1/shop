package com.shop.order.spec;

import com.shop.order.Order;
import com.shop.order.OrderStatus;
import org.springframework.data.jpa.domain.Specification;

public final class OrderSpecifications {

    private OrderSpecifications() {
    }

    public static Specification<Order> hasStatus(OrderStatus status) {
        if (status == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Order> hasUserId(Long userId) {
        if (userId == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
    }
}
