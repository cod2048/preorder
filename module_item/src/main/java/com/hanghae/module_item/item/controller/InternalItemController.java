package com.hanghae.module_item.item.controller;

import com.hanghae.module_item.item.dto.request.ReduceStockRequest;
import com.hanghae.module_item.item.dto.response.StockResponse;
import com.hanghae.module_item.item.service.ItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/internal/items")
public class InternalItemController {
    private final ItemService itemService;


    public InternalItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{itemNum}")
    public ResponseEntity<StockResponse> getItemStocks(@PathVariable("itemNum") Long itemNum) {
        Long stocks = itemService.getItemStocks(itemNum);
        StockResponse stockResponse = new StockResponse(itemNum, stocks);

        return ResponseEntity.ok(stockResponse);
    }

    @PostMapping
    public ResponseEntity<StockResponse> reduceItemStocks(@RequestBody ReduceStockRequest reduceStockRequest) {
        StockResponse stockResponse = itemService.reduceItemStocks(reduceStockRequest);
        return ResponseEntity.ok(stockResponse);
    }
}
