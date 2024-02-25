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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final ItemClient itemClient;
    private final StockClient stockClient;

    public OrderService(OrderRepository orderRepository, ItemClient itemClient, StockClient stockClient) {
        this.orderRepository = orderRepository;
        this.itemClient = itemClient;
        this.stockClient = stockClient;
    }

    public OrderResponse getOrderDetails(Long orderNum) {
        Order targetOrder = orderRepository.findById(orderNum)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        return new OrderResponse(targetOrder.getOrderNum(), targetOrder.getBuyerNum(), targetOrder.getItemNum(), targetOrder.getQuantity(), targetOrder.getPrice(), targetOrder.getStatus());
    }

    @Transactional
    public OrderResponse create(CreateOrderRequest createOrderRequest) {

        ItemDetailsResponse itemDetailsResponse = itemClient.getItemDetails(createOrderRequest.getItemNum()); // 아이템 정보

        LocalDateTime availableAt = itemDetailsResponse.getAvailableAt();
        LocalDateTime endAt = itemDetailsResponse.getEndAt();

        if(itemDetailsResponse.getStock() < createOrderRequest.getQuantity()) {
            throw new CustomException(ErrorCode.NOT_ENOUGH_STOCK);
        }

        if(isNotPreOrderTime(availableAt, endAt)) {
            throw new CustomException(ErrorCode.NOT_AVAILABLE_TIME);
        }

        StockDto reduceStockDto = new StockDto(createOrderRequest.getItemNum(), createOrderRequest.getQuantity());

        stockClient.reduceStocks(reduceStockDto);

        Order order = Order.create(createOrderRequest);

        Order savedOrder = orderRepository.save(order);

        double chance = Math.random();
        if (chance < 0.2) {
            StockDto increaseStockRequest = new StockDto(savedOrder.getItemNum(), savedOrder.getQuantity());
            stockClient.increaseStocks(increaseStockRequest);
            savedOrder.updateStatus(Order.OrderStatus.CANCELED);
        } else {
            savedOrder.updateStatus(Order.OrderStatus.IN_PROGRESS);
        }

        return new OrderResponse(savedOrder.getOrderNum(), savedOrder.getBuyerNum(), savedOrder.getItemNum(), savedOrder.getQuantity(), savedOrder.getPrice(), savedOrder.getStatus());
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

        if (targetOrder.getStatus().equals(Order.OrderStatus.FAILED_CUSTOMER)) {
            throw new CustomException(ErrorCode.FAILED_ORDER);
        }

        StockDto increaseStockRequest = new StockDto(targetOrder.getItemNum(), targetOrder.getQuantity());

        stockClient.increaseStocks(increaseStockRequest);

        targetOrder.delete();

        return new OrderResponse(targetOrder.getOrderNum(), targetOrder.getBuyerNum(), targetOrder.getItemNum(), targetOrder.getQuantity(), targetOrder.getPrice(), targetOrder.getStatus());
    }

    @Transactional
    public OrderResponse failedByCustomer(Long orderNum) {
        Order targetOrder = orderRepository.findById(orderNum)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        if (targetOrder.getStatus().equals(Order.OrderStatus.CANCELED)) {
            throw new CustomException(ErrorCode.CANCELED_ORDER);
        }

        if (targetOrder.getStatus().equals(Order.OrderStatus.FAILED_CUSTOMER)) {
            throw new CustomException(ErrorCode.FAILED_ORDER);
        }

        StockDto increaseStockRequest = new StockDto(targetOrder.getItemNum(), targetOrder.getQuantity());

        stockClient.increaseStocks(increaseStockRequest);

        targetOrder.failed();

        return new OrderResponse(targetOrder.getOrderNum(), targetOrder.getBuyerNum(), targetOrder.getItemNum(), targetOrder.getQuantity(), targetOrder.getPrice(), targetOrder.getStatus());
    }

    @Transactional
    public OrderResponse completeOrder(Long orderNum) {
        Order targetOrder = orderRepository.findById(orderNum)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        if (targetOrder.getStatus().equals(Order.OrderStatus.CANCELED)) {
            throw new CustomException(ErrorCode.CANCELED_ORDER);
        }

        if (targetOrder.getStatus().equals(Order.OrderStatus.FAILED_CUSTOMER)) {
            throw new CustomException(ErrorCode.FAILED_ORDER);
        }

        targetOrder.complete();

        return new OrderResponse(targetOrder.getOrderNum(), targetOrder.getBuyerNum(), targetOrder.getItemNum(), targetOrder.getQuantity(), targetOrder.getPrice(), targetOrder.getStatus());
    }

}
