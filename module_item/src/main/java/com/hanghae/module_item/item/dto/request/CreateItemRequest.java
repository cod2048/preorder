package com.hanghae.module_item.item.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class CreateItemRequest {
    private Long sellerNum;
    private String title;
    private String description;
    private Long price;
    private Long stock;
    private LocalDateTime availableAt;
    private LocalDateTime endAt;
}
