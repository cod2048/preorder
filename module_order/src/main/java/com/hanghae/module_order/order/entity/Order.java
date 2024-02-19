package com.hanghae.module_order.order.entity;

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
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_num")
    private Long orderNum;

    @Column(name = "buyer_num", nullable = false)
    private Long buyerNum;

    @Column(name = "item_num", nullable = false)
    private Long itemNum;

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    @Column(name = "status")
    private OrderStatus status;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Order(Long buyerNum, Long itemNum, Long quantity, OrderStatus status) {
        this.buyerNum = buyerNum;
        this.itemNum = itemNum;
        this.quantity = quantity;
        this.status = status;
    }

    public enum OrderStatus {
        INITIATED,
        IN_PROGRESS,
        COMPLETED,
        FAILED,
        CANCELED
    }
}
