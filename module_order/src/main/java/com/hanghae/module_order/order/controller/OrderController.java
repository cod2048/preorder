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
    public ResponseEntity<ApiResponse<OrderResponse>> create(@RequestBody CreateOrderRequest createOrderRequest) {
        OrderResponse orderResponse = orderService.create(createOrderRequest);

        ApiResponse<OrderResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "주문 진입",
                orderResponse
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

    @DeleteMapping("/delete/{orderNum}")
    public ResponseEntity<ApiResponse<OrderResponse>> delete(@PathVariable Long orderNum) {
        OrderResponse orderResponse = orderService.delete(orderNum);

        ApiResponse<OrderResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "주문 취소 성공",
                orderResponse
        );

        return ResponseEntity.ok().body(response);
    }

}
