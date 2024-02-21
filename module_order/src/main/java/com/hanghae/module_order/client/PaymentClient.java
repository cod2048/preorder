package com.hanghae.module_order.client;

import com.hanghae.module_order.client.dto.request.CreatePaymentRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "paymentClient", url = "${feign.paymentClient.url}")
public interface PaymentClient {

    @RequestMapping(method = RequestMethod.POST, value = "/api/v1/internal/payments", consumes = "application/json")
    void createPayment(CreatePaymentRequest createPaymentRequest);
}
