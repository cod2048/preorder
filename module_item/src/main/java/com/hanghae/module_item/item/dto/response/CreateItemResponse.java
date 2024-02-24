package com.hanghae.module_item.item.dto.response;

import com.hanghae.module_item.client.dto.StockDto;
import com.hanghae.module_item.item.entity.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class CreateItemResponse {
    private Item item;
    private StockDto stockDto;
}
