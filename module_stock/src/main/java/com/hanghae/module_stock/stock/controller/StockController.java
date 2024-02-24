package com.hanghae.module_stock.stock.controller;

import com.hanghae.module_stock.common.dto.response.ApiResponse;
import com.hanghae.module_stock.stock.dto.StockDto;
import com.hanghae.module_stock.stock.service.StockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stocks")
public class StockController {
    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PutMapping("/{itemNum}")
    public ResponseEntity<ApiResponse<StockDto>> updateStocks(@PathVariable Long itemNum, StockDto stockRequest) {
        StockDto responseDto = stockService.updateStocks(stockRequest);
        ApiResponse<StockDto> response = new ApiResponse<>(
                HttpStatus.OK,
                "재고 수정 성공",
                responseDto
        );
        return ResponseEntity.ok(response);
    }
}
