package com.hanghae.module_item.item.controller;

import com.hanghae.module_item.item.dto.request.UpdateStockRequest;
import com.hanghae.module_item.item.dto.response.ItemDetailsResponse;
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
    public ResponseEntity<ItemDetailsResponse> getItemDetails(@PathVariable("itemNum") Long itemNum) {

        ItemDetailsResponse itemDetailsResponse = itemService.getItemDetails(itemNum);

        return ResponseEntity.ok(itemDetailsResponse);
    }

    @PutMapping("/reduce")
    public ResponseEntity<StockResponse> reduceItemStocks(@RequestBody UpdateStockRequest updateStockRequest) {
        StockResponse stockResponse = itemService.reduceItemStocks(updateStockRequest);
        return ResponseEntity.ok(stockResponse);
    }

    @PutMapping("/increase")
    public ResponseEntity<StockResponse> increaseItemStocks(@RequestBody UpdateStockRequest updateStockRequest) {
        StockResponse stockResponse = itemService.increaseItemStocks(updateStockRequest);
        return ResponseEntity.ok(stockResponse);
    }
}
