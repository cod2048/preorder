package com.hanghae.modlue_payment.payment.service;

import com.hanghae.modlue_payment.payment.dto.response.PaymentDetailsResponse;
import com.hanghae.modlue_payment.payment.entity.Payment;
import com.hanghae.modlue_payment.payment.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public PaymentDetailsResponse getPaymentdetails(Long orderNum) {
        Payment payment = paymentRepository.findById(orderNum)
                .orElseThrow(() -> new IllegalArgumentException("order not exist"));

        return new PaymentDetailsResponse(payment.getOrderNum(), payment.getBuyerNum(), payment.getQuantity(), payment.getPrice(), payment.getCreatedAt());
    }
}
