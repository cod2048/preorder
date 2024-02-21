package com.hanghae.module_item.item.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class ItemDetailsResponse {
    private Long itemNum;
    private Long sellerNum;
    private String title;
    private String description;
    private Long price;
    private Long stock;
    private LocalDateTime availableAt;
    private LocalDateTime endAt;
}
