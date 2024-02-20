package com.hanghae.modlue_payment.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class PaymentDetailsResponse {
    private Long orderNum;
    private Long buyerNum;
    private Long quantity;
    private Long price;
    private LocalDateTime createdAt;
}
