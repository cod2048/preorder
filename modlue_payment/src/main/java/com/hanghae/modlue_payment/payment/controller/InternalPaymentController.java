package com.hanghae.modlue_payment.payment.controller;

import com.hanghae.modlue_payment.payment.dto.request.CreatePaymentRequest;
import com.hanghae.modlue_payment.payment.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/internal/payments")
@Slf4j
public class InternalPaymentController {
    private final PaymentService paymentService;

    public InternalPaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<?> createPayment(@RequestBody CreatePaymentRequest createPaymentRequest){
        log.info("createPayment 컨트롤러 진입");
        paymentService.createPayment(createPaymentRequest);
        return ResponseEntity.ok().body("create payment success");
    }
}
