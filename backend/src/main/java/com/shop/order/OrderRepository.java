package com.shop.order;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    Page<Order> findByUserId(Long userId, Pageable pageable);

    Optional<Order> findByIdAndUserId(Long id, Long userId);
}
