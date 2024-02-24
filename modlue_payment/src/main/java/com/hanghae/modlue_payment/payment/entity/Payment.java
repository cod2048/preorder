package com.hanghae.modlue_payment.payment.entity;

import com.hanghae.modlue_payment.payment.dto.request.CreatePaymentRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_num")
    private Long paymentNum;

    @Column(name = "order_num", nullable = false)
    private Long orderNum;

    @Column(name = "buyer_num", nullable = false)
    private Long buyerNum;

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name= "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    public Payment(Long orderNum, Long buyerNum, Long quantity, BigDecimal price) {
        this.orderNum = orderNum;
        this.buyerNum = buyerNum;
        this.quantity = quantity;
        this.price = price;
    }

    public static Payment create(Long orderNum, Long buyerNum, Long quantity, BigDecimal price) {
        return Payment.builder()
                .orderNum(orderNum)
                .buyerNum(buyerNum)
                .quantity(quantity)
                .price(price)
                .build();
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

}
