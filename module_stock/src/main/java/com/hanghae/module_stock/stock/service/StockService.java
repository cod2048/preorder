package com.hanghae.module_stock.stock.service;

import com.hanghae.module_stock.common.exception.CustomException;
import com.hanghae.module_stock.common.exception.ErrorCode;
import com.hanghae.module_stock.stock.dto.StockDto;
import com.hanghae.module_stock.stock.entity.Stock;
import com.hanghae.module_stock.stock.repository.StockRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class StockService {

    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Transactional
    public StockDto create(StockDto requestDto) {
//        log.info("stockSerivce, requestDto:{}",  requestDto.getItemNum());
//        log.info("stockService, requestDto: {}", requestDto.getStock());

        Stock newStock = Stock.create(requestDto.getItemNum(), requestDto.getStock());
//        log.info("stockSerivce, newStock: {}",  newStock.getStock());

        Stock savedStock = stockRepository.save(newStock);
//        log.info("stockSerivce, savedStock:{}",  savedStock.getStock());

        return new StockDto(savedStock.getItemNum(), savedStock.getStock());
    }

    @Transactional
    public synchronized StockDto getStocks(Long itemNum) {
        Stock stock = stockRepository.findAndLockById(itemNum);

        return new StockDto(stock.getItemNum(), stock.getStock());
    }

    @Transactional
    public void deleteStocks(Long itemNum) {
        Stock targetStock = stockRepository.findById(itemNum)
                        .orElseThrow(() -> new CustomException(ErrorCode.ITEM_STOCK_NOT_FOUND));

        stockRepository.delete(targetStock);
    }
    @Transactional
    public synchronized StockDto increaseStocks(StockDto requestDto) {
        Stock targetStock = stockRepository.findAndLockById(requestDto.getItemNum());

        if (targetStock == null) {
            throw new CustomException(ErrorCode.ITEM_STOCK_NOT_FOUND);
        }

        Long originalStock = targetStock.getStock();

        targetStock.update(originalStock + requestDto.getStock());

        return new StockDto(targetStock.getItemNum(), targetStock.getStock());
    }

    @Transactional
    public synchronized StockDto reduceStocks(StockDto requestDto) {
        Stock targetStock = stockRepository.findAndLockById(requestDto.getItemNum());

        if (targetStock == null) {
            throw new CustomException(ErrorCode.ITEM_STOCK_NOT_FOUND);
        }

        Long originalStock = targetStock.getStock();

        if (originalStock == 0) {
            throw new CustomException(ErrorCode.NOT_ENOUGH_STOCK);
        }

        targetStock.update(originalStock - requestDto.getStock());

        return new StockDto(targetStock.getItemNum(), targetStock.getStock());
    }

    @Transactional
    public synchronized StockDto updateStocks(StockDto requestDto) {
        Stock targetStock = stockRepository.findAndLockById(requestDto.getItemNum());

        if (targetStock == null) {
            throw new CustomException(ErrorCode.ITEM_STOCK_NOT_FOUND);
        }

        targetStock.update(requestDto.getStock());

        return new StockDto(targetStock.getItemNum(), targetStock.getStock());
    }

}
