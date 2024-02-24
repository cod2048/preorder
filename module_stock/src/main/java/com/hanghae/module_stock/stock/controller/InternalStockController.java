package com.hanghae.module_stock.stock.controller;

import com.hanghae.module_stock.stock.dto.StockDto;
import com.hanghae.module_stock.stock.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/internal/stocks")
@Slf4j
public class InternalStockController {
    private final StockService stockService;

    public InternalStockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping
    public ResponseEntity<StockDto> createStocks(@RequestBody StockDto stockRequest) {
        log.info("stockController : {}", stockRequest.getStock());
        StockDto responseDto = stockService.create(stockRequest);

        return ResponseEntity.ok().body(responseDto);
    }

    @GetMapping("/{itemNum}")
    public ResponseEntity<StockDto> getStocks(@PathVariable Long itemNum) {

        StockDto stockDto = stockService.getStocks(itemNum);

        return ResponseEntity.ok().body(stockDto);
    }

    @DeleteMapping("/{itemNum}")
    public ResponseEntity<?> deleteStocks(@PathVariable Long itemNum) {
        stockService.deleteStocks(itemNum);

        return ResponseEntity.ok().body("delete success");
    }

    @PutMapping("/increase/{itemNum}")
    public ResponseEntity<StockDto> increaseStocks(@PathVariable Long itemNum, @RequestBody StockDto requestDto) {

        StockDto responseDto = stockService.increaseStocks(requestDto);

        return ResponseEntity.ok().body(responseDto);
    }


    @PutMapping("/reduce/{itemNum}")
    public ResponseEntity<StockDto> reduceStocks(@PathVariable Long itemNum, @RequestBody StockDto requestDto) {

        StockDto responseDto = stockService.reduceStocks(requestDto);

        return ResponseEntity.ok().body(responseDto);
    }

}
