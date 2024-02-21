package com.hanghae.module_item.item.controller;

import com.hanghae.module_item.common.dto.response.ApiResponse;
import com.hanghae.module_item.item.dto.request.CreateItemRequest;
import com.hanghae.module_item.item.dto.response.CreateItemResponse;
import com.hanghae.module_item.item.dto.response.ItemDetailsResponse;
import com.hanghae.module_item.item.service.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ApiResponse<CreateItemResponse>> create(@RequestBody CreateItemRequest createItemRequest) {
        ApiResponse<CreateItemResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "상품 등록 성공",
                itemService.create(createItemRequest)
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<String>>> getAllItems(){
        ApiResponse<List<String>> response = new ApiResponse<>(
                HttpStatus.OK,
                "전체 상품 조회 결과",
                itemService.getAllItems()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{itemNum}")
    public ResponseEntity<ApiResponse<ItemDetailsResponse>> getItemDetails(@PathVariable Long itemNum){
        ApiResponse<ItemDetailsResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "상품 상세 조회 결과",
                itemService.getItemDetails(itemNum)
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/stocks/{itemNum}")
    public ResponseEntity<ApiResponse<Long>> getItemStocks(@PathVariable Long itemNum) {
        ApiResponse<Long> response = new ApiResponse<>(
                HttpStatus.OK,
                "상품 재고",
                itemService.getItemStocks(itemNum)
        );
        return ResponseEntity.ok(response);
    }
}
