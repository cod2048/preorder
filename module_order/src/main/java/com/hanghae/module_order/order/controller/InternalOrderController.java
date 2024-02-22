package com.hanghae.module_order.order.controller;

import com.hanghae.module_order.order.dto.response.CancelOrderResponse;
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

    @PutMapping("/{orderNum}")
    public ResponseEntity<CancelOrderResponse> cancelOrder(@PathVariable("orderNum") Long orderNum) {
        CancelOrderResponse cancelOrderResponse = orderService.cancelOrder(orderNum);

        return ResponseEntity.ok(cancelOrderResponse);
    }
}
