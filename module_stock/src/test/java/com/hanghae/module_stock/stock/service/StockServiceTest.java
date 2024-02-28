package com.hanghae.module_stock.stock.service;

import com.hanghae.module_stock.common.exception.CustomException;
import com.hanghae.module_stock.common.exception.ErrorCode;
import com.hanghae.module_stock.stock.dto.StockDto;
import com.hanghae.module_stock.stock.entity.Stock;
import com.hanghae.module_stock.stock.repository.StockRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {
    @InjectMocks
    private StockService stockService;

    @Mock
    private StockRepository stockRepository;

    @Test
    @DisplayName("재고 생성 성공")
    void createStockSuccess() {
        // Given
        Long itemNum = 1L;
        Long stockAmount = 100L;
        StockDto requestDto = new StockDto(itemNum, stockAmount);
        Stock mockStock = new Stock(itemNum, stockAmount);

        when(stockRepository.save(any(Stock.class))).thenReturn(mockStock);

        // When
        StockDto resultDto = stockService.create(requestDto);

        // Then
        assertEquals(itemNum, resultDto.getItemNum());
        assertEquals(stockAmount, resultDto.getStock());
    }

    @Test
    @DisplayName("재고 조회 성공")
    void getStocksSuccess() {
        // Given
        Long itemNum = 1L;
        Long stockAmount = 100L;
        Stock mockStock = new Stock(itemNum, stockAmount);
        when(stockRepository.findAndLockById(itemNum)).thenReturn(mockStock);

        // When
        StockDto result = stockService.getStocks(itemNum);

        // Then
        assertEquals(itemNum, result.getItemNum());
        assertEquals(stockAmount, result.getStock());
    }

    @Nested
    @DisplayName("재고 삭제")
    class deleteStock {
        @Test
        @DisplayName("재고 삭제 성공")
        void deleteStocksSuccess() {
            // Given
            Long itemNum = 1L;
            Stock mockStock = new Stock(itemNum, 10L); // 예시 재고 객체
            when(stockRepository.findById(itemNum)).thenReturn(Optional.of(mockStock));

            // When
            stockService.deleteStocks(itemNum);

            // Then
            verify(stockRepository).delete(mockStock);
        }

        @Test
        @DisplayName("재고 삭제 실패 - 존재하지 않는 아이템")
        void deleteStocksFailure() {
            // Given
            Long itemNum = 999L; // 존재하지 않는 아이템 번호
            when(stockRepository.findById(itemNum)).thenReturn(Optional.empty());

            // When
            CustomException exception = assertThrows(CustomException.class, () -> stockService.deleteStocks(itemNum));

            // Then
            assertEquals(ErrorCode.ITEM_STOCK_NOT_FOUND, exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("재고 증가")
    class increaseStock {
        @Test
        @DisplayName("재고 증가 성공")
        void increaseStocksSuccess() {
            // Given
            Long itemNum = 1L;
            Long originalStock = 100L;
            Long increaseAmount = 50L;
            Stock mockStock = new Stock(itemNum, originalStock);
            when(stockRepository.findAndLockById(itemNum)).thenReturn(mockStock);

            // When
            StockDto result = stockService.increaseStocks(new StockDto(itemNum, increaseAmount));

            // Then
            assertEquals(itemNum, result.getItemNum());
            assertEquals(originalStock + increaseAmount, result.getStock());
        }

        @Test
        @DisplayName("재고 증가 실패 - 재고 미존재")
        void increaseStocksStockNotFound() {
            // Given
            Long itemNum = 999L;
            Long increaseAmount = 50L;
            when(stockRepository.findAndLockById(itemNum)).thenReturn(null);

            // When
            CustomException exception = assertThrows(CustomException.class, () -> {
                stockService.increaseStocks(new StockDto(itemNum, increaseAmount));
            });

            // Then
            assertEquals(ErrorCode.ITEM_STOCK_NOT_FOUND, exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("재고 감소")
    class decreaseStock {
        @Test
        @DisplayName("재고 감소 성공")
        void reduceStocksSuccess() {
            // Given
            Long itemNum = 1L;
            Long originalStock = 100L;
            Long reduceAmount = 50L;
            Stock mockStock = new Stock(itemNum, originalStock);
            when(stockRepository.findAndLockById(itemNum)).thenReturn(mockStock);

            // When
            StockDto result = stockService.reduceStocks(new StockDto(itemNum, reduceAmount));

            // Then
            assertEquals(itemNum, result.getItemNum());
            assertEquals(originalStock - reduceAmount, result.getStock());
        }

        @Test
        @DisplayName("재고 감소 실패 - 아이템 미존재")
        void reduceStocksStockNotFound() {
            // Given
            Long itemNum = 999L;
            Long reduceAmount = 50L;
            when(stockRepository.findAndLockById(itemNum)).thenReturn(null);

            // When
            CustomException exception = assertThrows(CustomException.class, () -> stockService.reduceStocks(new StockDto(itemNum, reduceAmount)));

            // Then
            assertEquals(ErrorCode.ITEM_STOCK_NOT_FOUND, exception.getErrorCode());
        }

        @Test
        @DisplayName("재고 감소 실패 - 재고 부족")
        void reduceStocksNotEnoughStock() {
            // Given
            Long itemNum = 1L;
            Long originalStock = 0L;
            Stock mockStock = new Stock(itemNum, originalStock);
            when(stockRepository.findAndLockById(itemNum)).thenReturn(mockStock);

            // When
            CustomException exception = assertThrows(CustomException.class, () -> stockService.reduceStocks(new StockDto(itemNum, 50L)));

            // Then
            assertEquals(ErrorCode.NOT_ENOUGH_STOCK, exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("재고 수정")
    class updateStocks {
        @Test
        @DisplayName("재고 수정 성공")
        void updateStocksSuccess() {
            // Given
            Long itemNum = 1L;
            Long updatedStock = 150L;
            Stock mockStock = new Stock(itemNum, 100L);
            when(stockRepository.findAndLockById(itemNum)).thenReturn(mockStock);

            // When
            StockDto resultDto = stockService.updateStocks(new StockDto(itemNum, updatedStock));

            // Then
            assertEquals(itemNum, resultDto.getItemNum());
            assertEquals(updatedStock, resultDto.getStock());
        }

        @Test
        @DisplayName("재고 수정 실패 - 아이템 미존재")
        void updateStocksStockNotFound() {
            // Given
            Long itemNum = 999L;
            Long updateAmount = 150L;
            when(stockRepository.findAndLockById(itemNum)).thenReturn(null);

            // When
            CustomException exception = assertThrows(CustomException.class, () -> stockService.updateStocks(new StockDto(itemNum, updateAmount)));

            // Then
            assertEquals(ErrorCode.ITEM_STOCK_NOT_FOUND, exception.getErrorCode());
        }


    }

}