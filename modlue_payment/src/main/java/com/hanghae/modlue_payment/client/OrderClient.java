package com.hanghae.modlue_payment.client;

import com.hanghae.modlue_payment.client.dto.response.CancelOrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "orderClient", url = "${feign.orderClient.url}")
public interface OrderClient {
    @RequestMapping(method = RequestMethod.PUT, value = "/api/v1/internal/orders/{orderNum}")
    CancelOrderResponse cancelOrder(@PathVariable("orderNum") Long orderNum);
}
