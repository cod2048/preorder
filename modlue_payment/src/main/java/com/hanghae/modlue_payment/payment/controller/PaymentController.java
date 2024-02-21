package com.hanghae.modlue_payment.payment.controller;

import com.hanghae.modlue_payment.payment.dto.response.PaymentDetailsResponse;
import com.hanghae.modlue_payment.payment.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final PaymentService paymentService;


    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/{orderNum}")
    public ResponseEntity<PaymentDetailsResponse> getPaymentdetails(@PathVariable Long orderNum){

        PaymentDetailsResponse paymentDetailsResponse = paymentService.getPaymentdetails(orderNum);
        return ResponseEntity.ok().body(paymentDetailsResponse);
    }
}
