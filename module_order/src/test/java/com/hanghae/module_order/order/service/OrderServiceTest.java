package com.hanghae.module_order.order.service;

import com.hanghae.module_order.client.ItemClient;
import com.hanghae.module_order.client.StockClient;
import com.hanghae.module_order.client.dto.StockDto;
import com.hanghae.module_order.client.dto.response.ItemDetailsResponse;
import com.hanghae.module_order.common.exception.CustomException;
import com.hanghae.module_order.common.exception.ErrorCode;
import com.hanghae.module_order.order.dto.request.CreateOrderRequest;
import com.hanghae.module_order.order.dto.response.OrderResponse;
import com.hanghae.module_order.order.entity.Order;
import com.hanghae.module_order.order.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ItemClient itemClient;

    @Mock
    private StockClient stockClient;

    @Nested
    @DisplayName("주문 생성")
    class createOrder {
        @Test
        @DisplayName("주문 생성 성공")
        void createOrderSuccess() {

            // Given
            CreateOrderRequest request = new CreateOrderRequest(2L, 1L, 10L, new BigDecimal("100"));
            ItemDetailsResponse itemDetailsResponse = new ItemDetailsResponse(1L, 1L, "Item Name", "Description", new BigDecimal("100"), 10L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
            when(itemClient.getItemDetails(request.getItemNum())).thenReturn(itemDetailsResponse);
            when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            OrderResponse result = orderService.create(request);

            // Then
            assertNotNull(result);
            assertEquals(request.getBuyerNum(), result.getBuyerNum());
            assertEquals(request.getItemNum(), result.getItemNum());
            assertEquals(request.getQuantity(), result.getQuantity());
            assertEquals(request.getPrice(), result.getPrice());
        }

        @Test
        @DisplayName("주문 생성 실패 - 재고 부족")
        void createOrderFail() {
            CreateOrderRequest request = new CreateOrderRequest(2L, 1L, 10L, new BigDecimal("100"));
            ItemDetailsResponse itemDetailsResponse = new ItemDetailsResponse(1L, 1L, "Item Name", "Description", new BigDecimal("100"), 9L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));

            // When
            when(itemClient.getItemDetails(anyLong())).thenReturn(itemDetailsResponse);
            CustomException exception = assertThrows(CustomException.class, () -> {
                orderService.create(request);
            });

            //Then
            assertEquals(ErrorCode.NOT_ENOUGH_STOCK, exception.getErrorCode());
        }

        @Test
        @DisplayName("주문 생성 실패 - 예약 구매 시간이 아님")
        void createOrderFail_NotPreOrderTime() {
            // Given
            CreateOrderRequest request = new CreateOrderRequest(1L, 1L, 1L, new BigDecimal("100"));
            ItemDetailsResponse itemDetailsResponse = new ItemDetailsResponse(1L, 1L, "Item Name", "Description", new BigDecimal("100"), 10L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

            when(itemClient.getItemDetails(anyLong())).thenReturn(itemDetailsResponse);

            // When
            CustomException exception = assertThrows(CustomException.class, () -> {
                orderService.create(request);
            });

            // Then
            assertEquals(ErrorCode.NOT_AVAILABLE_TIME, exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("주문 정보 조회")
    class getOrderDetails {
        @Test
        @DisplayName("주문 상세 정보 조회 성공")
        void getOrderDetailsSuccess() {
            // Given
            Long orderNum = 1L;
            Order order = Order.builder()
                    .buyerNum(1L)
                    .itemNum(1L)
                    .quantity(1L)
                    .price(new BigDecimal("100"))
                    .status(Order.OrderStatus.INITIATED)
                    .build();

            when(orderRepository.findById(orderNum)).thenReturn(Optional.of(order));

            // When
            OrderResponse response = orderService.getOrderDetails(orderNum);

            // Then
            assertNotNull(response);
            assertEquals(1L, response.getBuyerNum());
            assertEquals(1L, response.getItemNum());
            assertEquals(1L, response.getQuantity());
            assertEquals(BigDecimal.valueOf(100), response.getPrice());
            assertEquals(Order.OrderStatus.INITIATED, response.getStatus());
        }

        @Test
        @DisplayName("주문 상세 정보 조회 실패 - 주문을 찾을 수 없음")
        void getOrderDetailsFail_NotFound() {
            // Given
            Long orderNum = 1L;

            when(orderRepository.findById(orderNum)).thenReturn(Optional.empty());

            // When
            CustomException exception = assertThrows(CustomException.class, () -> {
                orderService.getOrderDetails(orderNum);
            });

            // Then
            assertEquals(ErrorCode.ORDER_NOT_FOUND, exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("주문 삭제")
    class deleteOrder {
        @Test
        @DisplayName("주문 삭제 성공")
        void deleteOrderSuccess() {
            // Given
            Long orderNum = 1L;
            Order mockOrder = new Order(1L, 1L, 10L, new BigDecimal("100"), Order.OrderStatus.IN_PROGRESS);
            when(orderRepository.findById(orderNum)).thenReturn(Optional.of(mockOrder));

            // When
            OrderResponse response = orderService.delete(orderNum);

            // Then
            assertEquals(Order.OrderStatus.CANCELED, response.getStatus());
        }

        @Test
        @DisplayName("주문 삭제 실패 - 주문 미존재")
        void deleteOrderNotFound() {
            // Given
            Long orderNum = 1L;
            when(orderRepository.findById(orderNum)).thenReturn(Optional.empty());

            // When
            CustomException exception = assertThrows(CustomException.class, () -> orderService.delete(orderNum));

            // Then
            assertEquals(ErrorCode.ORDER_NOT_FOUND, exception.getErrorCode());
        }

        @Test
        @DisplayName("주문 삭제 실패 - 이미 삭제된 주문")
        void deleteOrderAlreadyCanceled() {
            // Given
            Long orderNum = 1L;
            Order mockOrder = new Order(1L, 1L, 10L, new BigDecimal("100"), Order.OrderStatus.CANCELED);
            when(orderRepository.findById(orderNum)).thenReturn(Optional.of(mockOrder));

            // When
            CustomException exception = assertThrows(CustomException.class, () -> orderService.delete(orderNum));

            // Then
            assertEquals(ErrorCode.CANCELED_ORDER, exception.getErrorCode());
        }

        @Test
        @DisplayName("주문 삭제 실패 - 고객 귀책 사유로 실패한 주문")
        void deleteOrderFailedByCustomer() {
            // Given
            Long orderNum = 1L;
            Order mockOrder = new Order(1L, 1L, 10L, new BigDecimal("100"), Order.OrderStatus.FAILED_CUSTOMER);
            when(orderRepository.findById(orderNum)).thenReturn(Optional.of(mockOrder));

            // When
            CustomException exception = assertThrows(CustomException.class, () -> orderService.delete(orderNum));

            // Then
            assertEquals(ErrorCode.FAILED_ORDER, exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("고객귀책 사유로 주문 실패 처리")
    class failedOrderByCustomer{
        @Test
        @DisplayName("주문 실패 처리 성공")
        void failedByCustomerSuccess() {
            // Given
            Long orderNum = 1L;
            Order mockOrder = new Order(1L, 1L, 10L, new BigDecimal("100"), Order.OrderStatus.IN_PROGRESS);
            when(orderRepository.findById(orderNum)).thenReturn(Optional.of(mockOrder));

            // When
            OrderResponse response = orderService.failedByCustomer(orderNum);

            // Then
            verify(stockClient, times(1)).increaseStocks(any(StockDto.class));
            assertEquals(Order.OrderStatus.FAILED_CUSTOMER, response.getStatus());
        }

        @Test
        @DisplayName("주문 실패 치리 실패 - 존재하지 않는 주문")
        void failedByCustomerOrderNotFound() {
            // Given
            Long orderNum = 1L;
            when(orderRepository.findById(orderNum)).thenThrow(new CustomException(ErrorCode.ORDER_NOT_FOUND));

            // When
            CustomException exception = assertThrows(CustomException.class, () -> orderService.failedByCustomer(orderNum));

            // Then
            assertEquals(ErrorCode.ORDER_NOT_FOUND, exception.getErrorCode());
        }

        @Test
        @DisplayName("주문 실패 치리 실패 - 취소된 주문")
        void failedByCustomerOrderAlreadyCanceled() {
            // Given
            Long orderNum = 1L;
            Order mockOrder = new Order(1L, 1L, 10L, new BigDecimal("100"), Order.OrderStatus.CANCELED);
            when(orderRepository.findById(orderNum)).thenReturn(Optional.of(mockOrder));

            // When
            CustomException exception = assertThrows(CustomException.class, () -> orderService.failedByCustomer(orderNum));

            // Then
            assertEquals(ErrorCode.CANCELED_ORDER, exception.getErrorCode());
        }

        @Test
        @DisplayName("주문 실패 치리 실패 - 이미 실패한 주문")
        void failedByCustomerOrderAlreadyFailed() {
            // Given
            Long orderNum = 1L;
            Order mockOrder = new Order(1L, 1L, 10L, new BigDecimal("100"), Order.OrderStatus.FAILED_CUSTOMER);
            when(orderRepository.findById(orderNum)).thenReturn(Optional.of(mockOrder));

            // When
            CustomException exception = assertThrows(CustomException.class, () -> orderService.failedByCustomer(orderNum));

            // Then
            assertEquals(ErrorCode.FAILED_ORDER, exception.getErrorCode());
        }

        @Nested
        @DisplayName("주문 완료")
        class completeOrder {
            @Test
            @DisplayName("주문 완료 처리 성공")
            void completeOrderSuccess() {
                // Given
                Long orderNum = 1L;
                Order mockOrder = new Order(1L, 1L, 10L, new BigDecimal("100"), Order.OrderStatus.IN_PROGRESS);
                when(orderRepository.findById(orderNum)).thenReturn(Optional.of(mockOrder));

                // When
                OrderResponse response = orderService.completeOrder(orderNum);

                // Then
                assertEquals(Order.OrderStatus.COMPLETED, response.getStatus());
            }

            @Test
            @DisplayName("주문 완료 실패 - 주문 미존재")
            void completeOrderNotFound() {
                // Given
                Long orderNum = 1L;
                when(orderRepository.findById(orderNum)).thenThrow(new CustomException(ErrorCode.ORDER_NOT_FOUND));

                // When
                CustomException exception = assertThrows(CustomException.class, () -> orderService.completeOrder(orderNum));

                // Then
                assertEquals(ErrorCode.ORDER_NOT_FOUND, exception.getErrorCode());
            }

            @Test
            @DisplayName("주문 완료 실패 - 취소된 주문")
            void completeOrderAlreadyCanceled() {
                // Given
                Long orderNum = 1L;
                Order mockOrder = new Order(1L, 1L, 10L, new BigDecimal("100"), Order.OrderStatus.CANCELED);
                when(orderRepository.findById(orderNum)).thenReturn(Optional.of(mockOrder));

                // When
                CustomException exception = assertThrows(CustomException.class, () -> orderService.completeOrder(orderNum));

                // Then
                assertEquals(ErrorCode.CANCELED_ORDER, exception.getErrorCode());
            }

            @Test
            @DisplayName("주문 완료 실패 - 고객귀책사유로 실패한 주문")
            void completeOrderFailedByCustomer() {
                // Given
                Long orderNum = 1L;
                Order mockOrder = new Order(1L, 1L, 10L, new BigDecimal("100"), Order.OrderStatus.FAILED_CUSTOMER);
                when(orderRepository.findById(orderNum)).thenReturn(Optional.of(mockOrder));

                // When
                CustomException exception = assertThrows(CustomException.class, () -> orderService.completeOrder(orderNum));

                // Then
                assertEquals(ErrorCode.FAILED_ORDER, exception.getErrorCode());
            }
        }

    }
}