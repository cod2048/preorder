package com.hanghae.module_item.item.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class StockResponse {
    private Long itemNum;
    private Long stock;
}
