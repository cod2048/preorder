package com.hanghae.module_stock.stock.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Stock {
    @Id
    @Column(name = "item_num")
    private Long itemNum;

    @Column(name = "stock")
    private Long stock;

    @Builder
    public Stock(Long itemNum, Long stock) {
        this.itemNum = itemNum;
        this.stock = stock;
    }

    public static Stock create(Long itemNum, Long stock) {
        return Stock.builder()
                .itemNum(itemNum)
                .stock(stock)
                .build();
    }
    
    public void updateStocks(Long newStock){
        this.stock = newStock;
    }
}
