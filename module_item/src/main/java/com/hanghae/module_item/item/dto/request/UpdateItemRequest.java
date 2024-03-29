package com.hanghae.module_item.item.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class UpdateItemRequest {
    private String title;
    private String description;
    private BigDecimal price;
    private LocalDateTime availableAt;
    private LocalDateTime endAt;
}
