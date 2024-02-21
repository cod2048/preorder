package com.hanghae.module_order.order.service;

import com.hanghae.module_order.client.ItemClient;
import com.hanghae.module_order.client.PaymentClient;
import com.hanghae.module_order.client.dto.request.CreatePaymentRequest;
import com.hanghae.module_order.client.dto.request.ReduceStockRequest;
import com.hanghae.module_order.client.dto.response.StockResponse;
import com.hanghae.module_order.order.dto.request.CreateOrderRequest;
import com.hanghae.module_order.order.entity.Order;
import com.hanghae.module_order.order.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            StockResponse originalStocks = itemClient.getItemStocks(order.getItemNum()); // 원래 재고
            ReduceStockRequest reduceStockRequest = new ReduceStockRequest(order.getItemNum(), order.getQuantity());
            StockResponse stockResponse = itemClient.updateItemStocks(reduceStockRequest); // 재고 감소

            if (Objects.equals(stockResponse.getStock(), originalStocks.getStock())) {
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


}
