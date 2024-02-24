package com.hanghae.module_item.item.controller;

import com.hanghae.module_item.item.dto.response.ItemDetailsResponse;
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

}
