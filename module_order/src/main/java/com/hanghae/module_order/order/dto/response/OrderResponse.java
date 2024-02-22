package com.hanghae.module_order.order.dto.response;

import com.hanghae.module_order.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class OrderResponse {
    private Long orderNum;
    private Long buyerNum;
    private Long itemNum;
    private Long quantity;
    private Order.OrderStatus status;
}
