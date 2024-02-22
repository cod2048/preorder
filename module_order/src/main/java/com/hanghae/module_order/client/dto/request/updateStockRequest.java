package com.hanghae.module_order.client.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class updateStockRequest {
    private Long itemNum;
    private Long quantity;
}
