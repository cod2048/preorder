package com.hanghae.module_stock.stock.repository;

import com.hanghae.module_stock.stock.entity.Stock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class StockRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StockRepository stockRepository;

    @Test
    @DisplayName("검색 성공")
    void findAndLockByIdSuccess() {
        // Given
        Stock newStock = new Stock(1L, 100L);
        entityManager.persist(newStock);
        entityManager.flush();

        // When
        Stock foundStock = stockRepository.findAndLockById(1L);

        // Then
        assertNotNull(foundStock);
        assertEquals(newStock.getItemNum(), foundStock.getItemNum());
        assertEquals(newStock.getStock(), foundStock.getStock());
    }

    @Test
    @DisplayName("검색 실패")
    void findAndLockByIdNotFound() {
        // When
        Stock foundStock = stockRepository.findAndLockById(999L);

        // Then
        assertNull(foundStock);
    }
}
