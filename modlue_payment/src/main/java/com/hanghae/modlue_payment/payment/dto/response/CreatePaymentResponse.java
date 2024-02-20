package com.hanghae.modlue_payment.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.LocalDateTime;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentResponse {
    private Long orderNum;
    private Long buyerNum;
    private Long quantity;
    private Long price;
    private LocalDateTime createAt;
}
