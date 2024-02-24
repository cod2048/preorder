package com.hanghae.module_order.order.controller;

import com.hanghae.module_order.order.dto.response.OrderResponse;
import com.hanghae.module_order.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/internal/orders")
public class InternalOrderController {
    private final OrderService orderService;


    public InternalOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{orderNum}")
    public ResponseEntity<OrderResponse> getOrderDetails(@PathVariable Long orderNum) {
        OrderResponse orderResponse = orderService.getOrderDetails(orderNum);

        return ResponseEntity.ok().body(orderResponse);
    }

    @PutMapping("/cancel/{orderNum}")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long orderNum) {
        OrderResponse orderResponse = orderService.delete(orderNum);

        return ResponseEntity.ok().body(orderResponse);
    }

    @PutMapping("/fail/{orderNum}")
    public ResponseEntity<OrderResponse> failedByCustomer(@PathVariable Long orderNum) {
        OrderResponse orderResponse = orderService.failedByCustomer(orderNum);

        return ResponseEntity.ok().body(orderResponse);
    }

    @PutMapping("/complete/{orderNum}")
    public ResponseEntity<OrderResponse> completeOrder(@PathVariable Long orderNum) {
        OrderResponse orderResponse = orderService.completeOrder(orderNum);

        return ResponseEntity.ok().body(orderResponse);
    }


}
