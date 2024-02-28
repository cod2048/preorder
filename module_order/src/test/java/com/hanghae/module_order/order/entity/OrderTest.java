package com.hanghae.module_order.order.entity;

import com.hanghae.module_order.order.dto.request.CreateOrderRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderTest {

    @Test
    @DisplayName("주문 생성")
    void createOrder() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest(1L, 2L, 3L, new BigDecimal("100.00"));

        // When
        Order order = Order.create(request);

        // Then
        assertEquals(1L, order.getBuyerNum());
        assertEquals(2L, order.getItemNum());
        assertEquals(3L, order.getQuantity());
        assertEquals(new BigDecimal("100.00"), order.getPrice());
        assertEquals(Order.OrderStatus.INITIATED, order.getStatus());
    }

    @Test
    @DisplayName("주문 상태 업데이트")
    void updateStatus() {
        // Given
        Order order = Order.builder()
                .buyerNum(1L)
                .itemNum(2L)
                .quantity(3L)
                .price(new BigDecimal("100.00"))
                .status(Order.OrderStatus.INITIATED)
                .build();

        // When
        order.updateStatus(Order.OrderStatus.COMPLETED);

        // Then
        assertEquals(Order.OrderStatus.COMPLETED, order.getStatus());
    }

    @Test
    @DisplayName("주문 취소")
    void deleteOrder() {
        // Given
        Order order = Order.builder()
                .buyerNum(1L)
                .itemNum(2L)
                .quantity(3L)
                .price(new BigDecimal("100.00"))
                .status(Order.OrderStatus.INITIATED)
                .build();

        // When
        order.delete();

        // Then
        assertEquals(Order.OrderStatus.CANCELED, order.getStatus());
    }

    @Test
    @DisplayName("주문 완료")
    void completeOrder() {
        // Given
        Order order = Order.builder()
                .buyerNum(1L)
                .itemNum(2L)
                .quantity(3L)
                .price(new BigDecimal("100.00"))
                .status(Order.OrderStatus.IN_PROGRESS)
                .build();

        // When
        order.complete();

        // Then
        assertEquals(Order.OrderStatus.COMPLETED, order.getStatus());
    }
}
