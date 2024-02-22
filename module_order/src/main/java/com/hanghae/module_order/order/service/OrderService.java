package com.hanghae.module_order.order.service;

import com.hanghae.module_order.client.ItemClient;
import com.hanghae.module_order.client.PaymentClient;
import com.hanghae.module_order.client.dto.request.CreatePaymentRequest;
import com.hanghae.module_order.client.dto.request.updateStockRequest;
import com.hanghae.module_order.client.dto.response.ItemDetailsResponse;
import com.hanghae.module_order.client.dto.response.StockResponse;
import com.hanghae.module_order.common.exception.CustomException;
import com.hanghae.module_order.common.exception.ErrorCode;
import com.hanghae.module_order.order.dto.request.CreateOrderRequest;
import com.hanghae.module_order.order.dto.response.CancelOrderResponse;
import com.hanghae.module_order.order.dto.response.OrderResponse;
import com.hanghae.module_order.order.entity.Order;
import com.hanghae.module_order.order.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final ItemClient itemClient;
    private final PaymentClient paymentClient;

    public OrderService(OrderRepository orderRepository, ItemClient itemClient, PaymentClient paymentClient) {
        this.orderRepository = orderRepository;
        this.itemClient = itemClient;
        this.paymentClient = paymentClient;
    }

    @Transactional
    public OrderResponse create(CreateOrderRequest createOrderRequest) {
        ItemDetailsResponse itemDetailsResponse = itemClient.getItemDetails(createOrderRequest.getItemNum()); // 아이템 정보

        LocalDateTime availableAt = itemDetailsResponse.getAvailableAt();
        LocalDateTime endAt = itemDetailsResponse.getEndAt();

        if(isNotPreOrderTime(availableAt, endAt)) {
            throw new CustomException(ErrorCode.NOT_AVAILABLE_TIME);
        }

        Order order = Order.create(createOrderRequest);

        Order savedOrder = orderRepository.save(order);

        return new OrderResponse(savedOrder.getOrderNum(), savedOrder.getBuyerNum(), savedOrder.getItemNum(), savedOrder.getQuantity(), savedOrder.getStatus());
    }

    @Transactional
    public OrderResponse tryPayment(Long orderNum) {
        Order order = orderRepository.findById(orderNum)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        ItemDetailsResponse itemDetailsResponse = itemClient.getItemDetails(order.getItemNum()); // 아이템 정보

        double chance = Math.random();
        if (chance < 0.2) {
            order.updateStatus(Order.OrderStatus.FAILED_CUSTOMER);
        } else {
            order.updateStatus(Order.OrderStatus.IN_PROGRESS);
        }

        if (order.getStatus() == Order.OrderStatus.IN_PROGRESS) {
            Long originalStocks = itemDetailsResponse.getStock(); // 현재 재고
            updateStockRequest updateStockRequest = new updateStockRequest(order.getItemNum(), order.getQuantity()); // 재고 감소 요청
            StockResponse stockResponse = itemClient.reduceItemStocks(updateStockRequest); // 재고 감소 결과

            if (Objects.equals(stockResponse.getStock(), originalStocks)) {
                order.updateStatus(Order.OrderStatus.FAILED_QUANTITY);
            } else {
                order.updateStatus(Order.OrderStatus.COMPLETED);
//                log.info("주문성공, 주문상세정보 저장 전");
                CreatePaymentRequest createPaymentRequest = new CreatePaymentRequest(order.getOrderNum(), order.getBuyerNum(), order.getQuantity(), order.getPrice());
//                log.info("주문성공 createPaymentRequest: {}", createPaymentRequest);
                paymentClient.createPayment(createPaymentRequest);

            }
        }

        return new OrderResponse(order.getOrderNum(), order.getBuyerNum(), order.getItemNum(), order.getQuantity(), order.getStatus());
    }

    public boolean isNotPreOrderTime(LocalDateTime availableAt, LocalDateTime endAt) {
        if (availableAt == null) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        return !(now.isAfter(availableAt) && now.isBefore(endAt));
    }

    @Transactional
    public OrderResponse delete(Long orderNum) {
        Order targetOrder = orderRepository.findById(orderNum)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        targetOrder.delete();

        return new OrderResponse(targetOrder.getOrderNum(), targetOrder.getBuyerNum(), targetOrder.getItemNum(), targetOrder.getQuantity(), targetOrder.getStatus());
    }

    @Transactional
    public CancelOrderResponse cancelOrder(Long orderNum) {
        Order targetOrder = orderRepository.findById(orderNum)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        updateStockRequest updateStockRequest = new updateStockRequest(targetOrder.getItemNum(), targetOrder.getQuantity());

        itemClient.increaseItemStocks(updateStockRequest);
        targetOrder.updateStatus(Order.OrderStatus.CANCELED);

        return new CancelOrderResponse(targetOrder.getOrderNum(), targetOrder.getStatus().toString());
    }

}
