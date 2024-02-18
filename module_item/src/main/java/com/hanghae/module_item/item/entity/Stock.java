package com.hanghae.module_item.item.entity;

import com.hanghae.module_item.item.dto.CreateItemRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
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

}