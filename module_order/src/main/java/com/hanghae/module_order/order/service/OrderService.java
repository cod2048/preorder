package com.hanghae.module_order.order.service;

import com.hanghae.module_order.client.ItemClient;
import com.hanghae.module_order.client.PaymentClient;
import com.hanghae.module_order.client.dto.request.CreatePaymentRequest;
import com.hanghae.module_order.client.dto.request.ReduceStockRequest;
import com.hanghae.module_order.client.dto.response.ItemDetailsResponse;
import com.hanghae.module_order.client.dto.response.StockResponse;
import com.hanghae.module_order.order.dto.request.CreateOrderRequest;
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
    public void create(CreateOrderRequest createOrderRequest) {
        ItemDetailsResponse itemDetailsResponse = itemClient.getItemDetails(createOrderRequest.getItemNum()); // 아이템 정보

        LocalDateTime availableAt = itemDetailsResponse.getAvailableAt();

        if(isNotPreOrderItem(availableAt)) {
            throw new IllegalArgumentException("not available time");
        }

        Order order = Order.builder()
                .buyerNum(createOrderRequest.getBuyerNum())
                .itemNum(createOrderRequest.getItemNum())
                .quantity(createOrderRequest.getQuantity())
                .price(createOrderRequest.getPrice())
                .status(Order.OrderStatus.INITIATED)
                .build();

        orderRepository.save(order);
    }

    @Transactional
    public Order.OrderStatus tryPayment(Long orderNum) {
        Order order = orderRepository.findById(orderNum)
                .orElseThrow(() -> new IllegalArgumentException("order not exist"));

        ItemDetailsResponse itemDetailsResponse = itemClient.getItemDetails(order.getItemNum()); // 아이템 정보

        double firstChance = Math.random();
        if (firstChance < 0.2) {
            order.updateStatus(Order.OrderStatus.CANCELED);
            return order.getStatus();
        }

        double secondChance = Math.random();
        if (secondChance < 0.2) {
            order.updateStatus(Order.OrderStatus.FAILED_CUSTOMER);
        } else {
            order.updateStatus(Order.OrderStatus.IN_PROGRESS);
        }

        if (order.getStatus() == Order.OrderStatus.IN_PROGRESS) {
            Long originalStocks = itemDetailsResponse.getStock(); // 현재 재고
            ReduceStockRequest reduceStockRequest = new ReduceStockRequest(order.getItemNum(), order.getQuantity()); // 재고 감소 요청
            StockResponse stockResponse = itemClient.updateItemStocks(reduceStockRequest); // 재고 감소 결과

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

        return order.getStatus();
    }

    @Transactional
    public boolean isNotPreOrderItem(LocalDateTime dateTime) {
        if (dateTime == null) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        return dateTime.isAfter(now);
    }

}
