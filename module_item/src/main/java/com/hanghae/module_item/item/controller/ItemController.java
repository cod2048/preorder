package com.hanghae.module_item.item.controller;

import com.hanghae.module_item.item.dto.request.CreateItemRequest;
import com.hanghae.module_item.item.dto.response.CreateItemResponse;
import com.hanghae.module_item.item.dto.response.GetItemResponse;
import com.hanghae.module_item.item.service.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<CreateItemResponse> create(@RequestBody CreateItemRequest createItemRequest) {
        return ResponseEntity.ok(itemService.create(createItemRequest));
    }

    @GetMapping
    public ResponseEntity<List<String>> getAllItems(){
        return ResponseEntity.ok(itemService.getAllItems());
    }

    @GetMapping("/{itemNum}")
    public ResponseEntity<GetItemResponse> getItemDetails(@PathVariable Long itemNum){
        return ResponseEntity.ok(itemService.getItemDetails(itemNum));
    }

    @GetMapping("/stocks/{itemNum}")
    public ResponseEntity<Long> getItemStocks(@PathVariable Long itemNum) {
        return ResponseEntity.ok(itemService.getItemStocks(itemNum));
    }
}
