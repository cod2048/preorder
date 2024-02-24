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
    public ResponseEntity<StockDto> createStocks(@RequestBody StockDto requestDto) {
//        log.info("stockController : {}", requestDto.getStock());
        StockDto responseDto = stockService.create(requestDto);

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

    @PutMapping("/increase")
    public ResponseEntity<StockDto> increaseStocks(@RequestBody StockDto requestDto) {

        StockDto responseDto = stockService.increaseStocks(requestDto);

        return ResponseEntity.ok().body(responseDto);
    }


    @PutMapping("/reduce")
    public ResponseEntity<StockDto> reduceStocks(@RequestBody StockDto requestDto) {

        StockDto responseDto = stockService.reduceStocks(requestDto);

        return ResponseEntity.ok().body(responseDto);
    }

}
