package com.hanghae.modlue_payment.client.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class CancelOrderResponse {
    private Long orderNum;
    private String status;
}