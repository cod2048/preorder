package com.hanghae.modlue_payment.payment.service;

import com.hanghae.modlue_payment.client.OrderClient;
import com.hanghae.modlue_payment.client.dto.response.OrderResponse;
import com.hanghae.modlue_payment.common.enums.OrderStatus;
import com.hanghae.modlue_payment.common.exception.CustomException;
import com.hanghae.modlue_payment.common.exception.ErrorCode;
import com.hanghae.modlue_payment.payment.dto.request.CreatePaymentRequest;
import com.hanghae.modlue_payment.payment.dto.response.PaymentDetailsResponse;
import com.hanghae.modlue_payment.payment.entity.Payment;
import com.hanghae.modlue_payment.payment.repository.PaymentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private OrderClient orderClient;

    @Mock
    private PaymentRepository paymentRepository;

    @Nested
    @DisplayName("결제 정보 조회")
    class getPaymentDetails {
        @Test
        @DisplayName("결제 상세 조회 성공")
        void getPaymentDetailsSuccess() {
            // Given
            Long orderNum = 1L;
            Payment mockPayment = Payment.create(orderNum, 1L, 10L, new BigDecimal("1000"));
            when(paymentRepository.findById(orderNum)).thenReturn(Optional.of(mockPayment));

            // When
            PaymentDetailsResponse response = paymentService.getPaymentdetails(orderNum);

            // Then
            assertNotNull(response);
            assertEquals(orderNum, response.getOrderNum());
        }

        @Test
        @DisplayName("결제 상세 조회 실패 - 결제 정보 없음")
        void getPaymentDetailsNotFound() {
            // Given
            Long orderNum = 1L;
            when(paymentRepository.findById(orderNum)).thenReturn(Optional.empty());

            // When
            CustomException ex = assertThrows(CustomException.class, () -> paymentService.getPaymentdetails(orderNum));

            // Then
            assertEquals(ErrorCode.ORDER_NOT_FOUND, ex.getErrorCode());
        }

        @Test
        @DisplayName("결제 상세 조회 실패 - 결제 정보 삭제됨")
        void getPaymentDetailsDeleted() {
            // Given
            Long orderNum = 1L;
            Payment mockPayment = Payment.create(orderNum, 1L, 10L, new BigDecimal("1000"));
            mockPayment.delete();
            when(paymentRepository.findById(orderNum)).thenReturn(Optional.of(mockPayment));

            // When
            CustomException ex = assertThrows(CustomException.class, () -> paymentService.getPaymentdetails(orderNum));

            // Then
            assertEquals(ErrorCode.DELETED_PAYMENT, ex.getErrorCode());
        }
    }

    @Nested
    @DisplayName("결제 생성")
    class createPayment {
        @Test
        @DisplayName("결제 생성 성공")
        void createPaymentSuccess() {
            // Given
            CreatePaymentRequest request = new CreatePaymentRequest(1L);
            OrderResponse inProgressOrder = new OrderResponse(1L, 1L, 1L, 10L, new BigDecimal("1000"), OrderStatus.IN_PROGRESS);
            when(orderClient.getOrderDetails(anyLong())).thenReturn(inProgressOrder);
            when(orderClient.completeOrder(anyLong())).thenReturn(inProgressOrder);
            when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            PaymentDetailsResponse response = paymentService.createPayment(request);

            // Then
            assertNotNull(response);
            assertEquals(1L, response.getOrderNum());
        }

        @Test
        @DisplayName("결제 생성 실패 - 잘못된 주문 상태")
        void createPaymentInvalidStatus() {
            // Given
            CreatePaymentRequest request = new CreatePaymentRequest(1L);
            OrderResponse invalidOrder = new OrderResponse(1L, 1L, 1L, 10L, new BigDecimal("1000"), OrderStatus.COMPLETED);
            when(orderClient.getOrderDetails(anyLong())).thenReturn(invalidOrder);

            // When
            CustomException ex = assertThrows(CustomException.class, () -> paymentService.createPayment(request));

            // Then
            assertEquals(ErrorCode.INVALID_REQUEST, ex.getErrorCode());
        }
    }

    @Nested
    @DisplayName("결제 취소")
    class deletePayment {
        @Test
        @DisplayName("결제 삭제 성공")
        void deletePaymentSuccess() {
            // Given
            Payment existingPayment = Payment.builder()
                    .orderNum(1L)
                    .buyerNum(1L)
                    .quantity(10L)
                    .price(new BigDecimal("1000"))
                    .build();
            when(paymentRepository.findById(anyLong())).thenReturn(Optional.of(existingPayment));

            // When
            paymentService.delete(1L);

            // Then
            verify(orderClient).cancelOrder(anyLong());
            assertNotNull(existingPayment.getDeletedAt());
        }

        @Test
        @DisplayName("결제 삭제 실패 - 결제 미존재")
        void deletePaymentNotFound() {
            // Given
            when(paymentRepository.findById(anyLong())).thenThrow(new CustomException(ErrorCode.ORDER_NOT_FOUND));

            // When
            CustomException ex = assertThrows(CustomException.class, () -> paymentService.delete(anyLong()));

            // Then
            assertEquals(ErrorCode.ORDER_NOT_FOUND, ex.getErrorCode());
        }

        @Test
        @DisplayName("결제 삭제 실패 - 이미 삭제된 결제")
        void deletePaymentAlreadyDeleted() {
            // Given
            Payment deletedPayment = Payment.builder()
                    .orderNum(1L)
                    .buyerNum(1L)
                    .quantity(10L)
                    .price(new BigDecimal("1000"))
                    .build();
            deletedPayment.delete();
            when(paymentRepository.findById(anyLong())).thenReturn(Optional.of(deletedPayment));

            // When
            CustomException ex = assertThrows(CustomException.class, () -> paymentService.delete(anyLong()));

            // Then
            assertEquals(ErrorCode.DELETED_PAYMENT, ex.getErrorCode());
        }
    }

}