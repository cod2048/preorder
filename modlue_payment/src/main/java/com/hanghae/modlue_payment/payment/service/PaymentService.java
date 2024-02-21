package com.hanghae.modlue_payment.payment.service;

import com.hanghae.modlue_payment.common.exception.CustomException;
import com.hanghae.modlue_payment.common.exception.ErrorCode;
import com.hanghae.modlue_payment.payment.dto.request.CreatePaymentRequest;
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
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        return new PaymentDetailsResponse(payment.getOrderNum(), payment.getBuyerNum(), payment.getQuantity(), payment.getPrice(), payment.getCreatedAt());
    }

//    @Transactional
    public void createPayment(CreatePaymentRequest createPaymentRequest) {
        log.info("createPayment 서비스 진입");
        log.info("createPaymentRequestId : {}", createPaymentRequest.getOrderNum());

        Payment payment = Payment.builder()
                .orderNum(createPaymentRequest.getOrderNum())
                .buyerNum(createPaymentRequest.getBuyerNum())
                .quantity(createPaymentRequest.getQuantity())
                .price(createPaymentRequest.getPrice())
                .build();

        log.info("payment 생성 : {}", payment);
        log.info("payment id : {}", payment.getOrderNum());

        Payment newPayment = paymentRepository.save(payment);

        log.info("payment 저장 : {}", newPayment);
    }
}
