package com.hanghae.module_item.client;

import com.hanghae.module_item.client.dto.StockDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "stockClient", url= "${feign.stockClient.url}")
public interface StockClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/v1/internal/stocks/{itemNum}", consumes = "application/json")
    StockDto getStocks(@PathVariable("itemNum") Long itemNum);

    @RequestMapping(method = RequestMethod.POST, value = "/api/v1/internal/stocks", consumes = "application/json")
    StockDto createStocks(@RequestBody StockDto requestDto);

    @RequestMapping(method = RequestMethod.PUT, value = "/api/v1/internal/stocks/increase", consumes = "application/json")
    StockDto increaseStocks(@RequestBody StockDto requestDto);

    @RequestMapping(method = RequestMethod.PUT, value = "/api/v1/internal/stocks/reduce", consumes = "application/json")
    StockDto reduceStocks(@RequestBody StockDto requestDto);

    @RequestMapping(method = RequestMethod.DELETE, value = "/api/v1/internal/stocks/{itemNum}", consumes = "application/json")
    void deleteStocks(@PathVariable("itemNum") Long itemNum);

}
