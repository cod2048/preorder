package com.hanghae.module_item.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class GetItemResponse {
    private String title;
    private String description;
    private Long price;
}
