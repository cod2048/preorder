package com.hanghae.modlue_payment.payment.controller;

import com.hanghae.modlue_payment.common.dto.response.ApiResponse;
import com.hanghae.modlue_payment.payment.dto.response.PaymentDetailsResponse;
import com.hanghae.modlue_payment.payment.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final PaymentService paymentService;


    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/{orderNum}")
    public ResponseEntity<ApiResponse<PaymentDetailsResponse>> getPaymentdetails(@PathVariable Long orderNum){
        PaymentDetailsResponse paymentDetailsResponse = paymentService.getPaymentdetails(orderNum);

        ApiResponse<PaymentDetailsResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "주문 정보 조회 결과",
                paymentDetailsResponse
        );

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{orderNum}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Long orderNum) {
        paymentService.delete(orderNum);

        ApiResponse<?> response = new ApiResponse<>(
                HttpStatus.OK,
                "주문 취소 성공",
                "."
        );

        return ResponseEntity.ok().body(response);
    }
}
