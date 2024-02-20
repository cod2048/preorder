package com.hanghae.module_order.order.service;

import com.hanghae.module_order.order.dto.request.CreateOrderRequest;
import com.hanghae.module_order.order.entity.Order;
import com.hanghae.module_order.order.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
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
    public Order.OrderStatus tryPayment(Long orderNum) {
        Order order = orderRepository.findById(orderNum)
                .orElseThrow(() -> new IllegalArgumentException("order not exist"));

        double chance = Math.random();
        if (chance < 0.2) {
            order.updateStatus(Order.OrderStatus.FAILED_CUSTOMER);
        } else {
            order.updateStatus(Order.OrderStatus.IN_PROGRESS);
        }

        // 수량 정보가져오기 추가

        return order.getStatus();
    }


}
