package com.hanghae.modlue_payment.client.dto.response;

import com.hanghae.modlue_payment.common.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class OrderResponse {
    private Long orderNum;
    private Long buyerNum;
    private Long itemNum;
    private Long quantity;
    private BigDecimal price;
    private OrderStatus status;
}
