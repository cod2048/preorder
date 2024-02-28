package com.hanghae.module_stock.stock.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StockTest {

    @Test
    @DisplayName("재고 생성")
    void createStock() {
        // Given
        Long itemNum = 1L;
        Long initialStock = 100L;

        // When
        Stock stock = Stock.create(itemNum, initialStock);

        // Then
        assertEquals(itemNum, stock.getItemNum());
        assertEquals(initialStock, stock.getStock());
    }

    @Test
    @DisplayName("재고 수량 업데이트")
    void updateStock() {
        // Given
        Long itemNum = 1L;
        Long initialStock = 100L;
        Stock stock = Stock.create(itemNum, initialStock);
        Long newStock = 150L;

        // When
        stock.update(newStock);

        // Then
        assertEquals(newStock, stock.getStock());
    }

}