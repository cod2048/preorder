package com.hanghae.module_item.item.entity;

import com.hanghae.module_item.item.dto.CreateItemRequest;
import jakarta.persistence.*;
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
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_num")
    private Long itemNum;

    @Column(name = "seller_num", nullable = false)
    private Long sellerNum;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "price", nullable = false)
    private Long price;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "available_at")
    private LocalDateTime availableAt;

    @Column(name = "end_at")
    private LocalDateTime endAt;

    @Builder
    public Item(Long sellerNum, String title, String description, Long price, LocalDateTime availableAt, LocalDateTime endAt) {
        this.sellerNum = sellerNum;
        this.title = title;
        this.description = description;
        this.price = price;
        this.availableAt = availableAt;
        this.endAt = endAt;
    }

}
