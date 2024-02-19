package com.hanghae.module_order.order.controller;

import com.hanghae.module_order.order.dto.request.CreateOrderRequest;
import com.hanghae.module_order.order.dto.request.CreateTryPaymentRequest;
import com.hanghae.module_order.order.entity.Order;
import com.hanghae.module_order.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateOrderRequest createOrderRequest) {
        orderService.create(createOrderRequest);
        return ResponseEntity.ok().body("order start");
    }

    @PostMapping("/try-payments")
    public ResponseEntity<?> tryPayment(@RequestBody CreateTryPaymentRequest createTryPaymentRequest) {
        Order.OrderStatus status = orderService.tryPayment(createTryPaymentRequest);

        return switch (status) {
            case IN_PROGRESS -> ResponseEntity.ok().body("order in progress");
            case FAILED_CUSTOMER -> ResponseEntity.status(HttpStatus.CONFLICT).body("order failed(customer)");
            default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error");
        };

    }

}
