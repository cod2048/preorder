package com.hanghae.modlue_payment.payment.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {
    @Test
    @DisplayName("결제 생성")
    void createPaymentSuccess() {
        // Given
        Long orderNum = 1L;
        Long buyerNum = 1L;
        Long quantity = 10L;
        BigDecimal price = new BigDecimal("1000");

        // When
        Payment payment = Payment.create(orderNum, buyerNum, quantity, price);

        // Then
        assertNotNull(payment);
        assertEquals(orderNum, payment.getOrderNum());
        assertEquals(buyerNum, payment.getBuyerNum());
        assertEquals(quantity, payment.getQuantity());
    }

    @Test
    @DisplayName("결제 삭제")
    void deletePaymentSetsDeletedAt() {
        // Given
        Payment payment = Payment.create(1L, 1L, 10L, new BigDecimal("1000"));
        assertNull(payment.getDeletedAt());

        // When
        payment.delete();

        // Then
        assertNotNull(payment.getDeletedAt());
        assertTrue(payment.getDeletedAt().isBefore(LocalDateTime.now().plusMinutes(1)));
        assertTrue(payment.getDeletedAt().isAfter(LocalDateTime.now().minusMinutes(1)));
    }
}