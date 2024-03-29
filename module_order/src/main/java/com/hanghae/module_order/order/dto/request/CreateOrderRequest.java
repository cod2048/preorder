package com.hanghae.module_order.order.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class CreateOrderRequest {
    private Long buyerNum;
    private Long itemNum;
    private Long quantity;
    private BigDecimal price;
}
