package com.hanghae.module_order.order.service;

import com.hanghae.module_order.client.ItemClient;
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

    public OrderService(OrderRepository orderRepository, ItemClient itemClient) {
        this.orderRepository = orderRepository;
        this.itemClient = itemClient;
    }

    @Transactional
    public void create(CreateOrderRequest createOrderRequest) {
        Order order = Order.builder()
                .buyerNum(createOrderRequest.getBuyerNum())
                .itemNum(createOrderRequest.getItemNum())
                .quantity(createOrderRequest.getQuantity())
                .status(Order.OrderStatus.INITIATED)
                .build();

        orderRepository.save(order);
    }

    @Transactional
    public Order.OrderStatus tryPayment(Long orderNum) {
        Order order = orderRepository.findById(orderNum)
                .orElseThrow(() -> new IllegalArgumentException("order not exist"));

        double chance = Math.random();
        if (chance < 0.2) {
            order.updateStatus(Order.OrderStatus.FAILED_CUSTOMER);
        } else {
            order.updateStatus(Order.OrderStatus.IN_PROGRESS);
        }

        if (order.getStatus() == Order.OrderStatus.IN_PROGRESS) {
            StockResponse originalStocks = itemClient.checkItemStocks(order.getItemNum());
            ReduceStockRequest reduceStockRequest = new ReduceStockRequest(order.getItemNum(), order.getQuantity());
            StockResponse stockResponse = itemClient.updateItemStocks(reduceStockRequest);

            if (Objects.equals(stockResponse.getStock(), originalStocks.getStock())) {
                order.updateStatus(Order.OrderStatus.FAILED_QUANTITY);
            } else {
                order.updateStatus(Order.OrderStatus.COMPLETED);
            }
        }

        return order.getStatus();
    }


}
