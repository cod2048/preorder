package com.hanghae.module_order.order.repository;

import com.hanghae.module_order.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
