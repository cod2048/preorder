package com.hanghae.module_order.order.service;

import com.hanghae.module_order.order.dto.request.CreateOrderRequest;
import com.hanghae.module_order.order.dto.request.CreateTryPaymentRequest;
import com.hanghae.module_order.order.entity.Order;
import com.hanghae.module_order.order.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public void create(CreateOrderRequest createOrderRequest) {
        Order order = Order.builder()
                .buyerNum(createOrderRequest.getBuyerNum())
                .itemNum(createOrderRequest.getItemNum())
                .quantity(createOrderRequest.getQuantity())
                .status(Order.OrderStatus.INITIATED)
                .build();

        orderRepository.save(order);
    }

    @Transactional
    public Order.OrderStatus tryPayment(CreateTryPaymentRequest createTryPaymentRequest) {
        Order order = orderRepository.findById(createTryPaymentRequest.getOrderNum())
                .orElseThrow(() -> new IllegalArgumentException("order not exist"));

        order.updateStatus(Order.OrderStatus.IN_PROGRESS);

        double chance = Math.random();
        if (chance < 0.2) {
            order.updateStatus(Order.OrderStatus.FAILED_CUSTOMER);
        } else {
            order.updateStatus(Order.OrderStatus.IN_PROGRESS);
        }

        return order.getStatus();
    }
}
