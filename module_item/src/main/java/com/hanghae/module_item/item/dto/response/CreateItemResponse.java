package com.hanghae.module_item.item.dto.response;

import com.hanghae.module_item.item.entity.Item;
import com.hanghae.module_item.item.entity.Stock;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class CreateItemResponse {
    private Item item;
    private Stock stock;
}
