package com.hanghae.modlue_payment.payment.repository;

import com.hanghae.modlue_payment.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
