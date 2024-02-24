package com.hanghae.module_order.order.service;

import com.hanghae.module_order.client.ItemClient;
import com.hanghae.module_order.client.PaymentClient;
import com.hanghae.module_order.client.StockClient;
import com.hanghae.module_order.client.dto.StockDto;
import com.hanghae.module_order.client.dto.request.CreatePaymentRequest;
import com.hanghae.module_order.client.dto.response.ItemDetailsResponse;
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
    private final StockClient stockClient;

    public OrderService(OrderRepository orderRepository, ItemClient itemClient, PaymentClient paymentClient, StockClient stockClient) {
        this.orderRepository = orderRepository;
        this.itemClient = itemClient;
        this.paymentClient = paymentClient;
        this.stockClient = stockClient;
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

        if (order.getStatus().equals(Order.OrderStatus.CANCELED)) {
            throw new CustomException(ErrorCode.CANCELED_ORDER);
        }

        ItemDetailsResponse itemDetailsResponse = itemClient.getItemDetails(order.getItemNum()); // 아이템 정보

        double chance = Math.random();
        if (chance < 0.2) {
            order.updateStatus(Order.OrderStatus.FAILED_CUSTOMER);
        } else {
            order.updateStatus(Order.OrderStatus.IN_PROGRESS);
        }

        if (order.getStatus() == Order.OrderStatus.IN_PROGRESS) {
            Long originalStocks = itemDetailsResponse.getStock(); // 현재 재고
            StockDto stockRequest = new StockDto(order.getItemNum(), order.getQuantity()); // 재고 감소 요청
            StockDto stockResponse = stockClient.reduceStocks(order.getItemNum(), stockRequest); // 재고 감소 결과

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

        if (targetOrder.getStatus().equals(Order.OrderStatus.CANCELED)) {
            throw new CustomException(ErrorCode.CANCELED_ORDER);
        }

        targetOrder.delete();

        return new OrderResponse(targetOrder.getOrderNum(), targetOrder.getBuyerNum(), targetOrder.getItemNum(), targetOrder.getQuantity(), targetOrder.getStatus());
    }

    @Transactional
    public CancelOrderResponse cancelOrder(Long orderNum) {
        Order targetOrder = orderRepository.findById(orderNum)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        if (targetOrder.getStatus().equals(Order.OrderStatus.CANCELED)) {
            throw new CustomException(ErrorCode.CANCELED_ORDER);
        }

        StockDto stockRequest = new StockDto(targetOrder.getItemNum(), targetOrder.getQuantity());

        stockClient.increaseStocks(targetOrder.getItemNum(), stockRequest);
        targetOrder.updateStatus(Order.OrderStatus.CANCELED);

        return new CancelOrderResponse(targetOrder.getOrderNum(), targetOrder.getStatus().toString());
    }

}
