package com.hanghae.module_order.client;

import com.hanghae.module_order.client.dto.request.ReduceStockRequest;
import com.hanghae.module_order.client.dto.response.StockResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "itemClient", url = "${feign.itemClient.url}")
public interface ItemClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/v1/internal/items/{itemNum}", consumes = "application/json")
    StockResponse getItemStocks(@PathVariable("itemNum") Long itemNum);

    @RequestMapping(method = RequestMethod.POST, value = "/api/v1/internal/items", consumes = "application/json")
    StockResponse updateItemStocks(@RequestBody ReduceStockRequest reduceStockRequest);


}
