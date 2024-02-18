package com.hanghae.module_item.item.repository;

import com.hanghae.module_item.item.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Long> {
}
