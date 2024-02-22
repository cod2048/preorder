package com.hanghae.module_order.order.controller;

import com.hanghae.module_order.common.dto.response.ApiResponse;
import com.hanghae.module_order.order.dto.request.CreateOrderRequest;
import com.hanghae.module_order.order.dto.response.OrderResponse;
import com.hanghae.module_order.order.entity.Order;
import com.hanghae.module_order.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Order>> create(@RequestBody CreateOrderRequest createOrderRequest) {
        Order newOrder = orderService.create(createOrderRequest);

        ApiResponse<Order> response = new ApiResponse<>(
                HttpStatus.OK,
                "주문 진입",
                newOrder
        );

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/try-payments/{orderNum}")
    public ResponseEntity<ApiResponse<OrderResponse>> tryPayment(@PathVariable Long orderNum) {
        OrderResponse orderResponse = orderService.tryPayment(orderNum);

        ApiResponse<OrderResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "주문 시도 결과",
                orderResponse
        );

        return ResponseEntity.ok().body(response);

    }

}
