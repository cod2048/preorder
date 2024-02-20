package com.hanghae.modlue_payment.payment.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Payment {
    @Id
    @Column(name = "order_num", nullable = false)
    private Long orderNum;

    @Column(name = "buyer_num", nullable = false)
    private Long buyerNum;

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    @Column(name = "price", nullable = false)
    private Long price;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name= "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    public Payment(Long orderNum, Long buyerNum, Long quantity, Long price) {
        this.orderNum = orderNum;
        this.buyerNum = buyerNum;
        this.quantity = quantity;
        this.price = price;
    }

}
