package com.hanghae.module_item.item.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ReduceStockRequest {
    private Long itemNum;
    private Long quantity;
}
