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
@Table(name = "orders")
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

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Order(Long buyerNum, Long itemNum, Long quantity, Long price, OrderStatus status) {
        this.buyerNum = buyerNum;
        this.itemNum = itemNum;
        this.quantity = quantity;
        this.price = price;
        this.status = status;
    }

    public enum OrderStatus {
        INITIATED,
        IN_PROGRESS,
        COMPLETED,
        FAILED_CUSTOMER,
        FAILED_QUANTITY,
        CANCELED
    }

    public void updateStatus(OrderStatus status) {
        this.status = status;
    }

}
