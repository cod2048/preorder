package com.hanghae.modlue_payment.payment.service;

import com.hanghae.modlue_payment.client.OrderClient;
import com.hanghae.modlue_payment.client.dto.response.OrderResponse;
import com.hanghae.modlue_payment.common.enums.OrderStatus;
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

    private final OrderClient orderClient;

    public PaymentService(PaymentRepository paymentRepository, OrderClient orderClient) {
        this.paymentRepository = paymentRepository;
        this.orderClient = orderClient;
    }

    public PaymentDetailsResponse getPaymentdetails(Long orderNum) {
        Payment payment = paymentRepository.findById(orderNum)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        if (payment.getDeletedAt() != null) {
            throw new CustomException(ErrorCode.DELETED_PAYMENT);
        }

        return new PaymentDetailsResponse(
                payment.getPaymentNum(),
                payment.getOrderNum(),
                payment.getBuyerNum(),
                payment.getQuantity(),
                payment.getPrice(),
                payment.getCreatedAt()
                );
    }

    @Transactional
    public PaymentDetailsResponse createPayment(CreatePaymentRequest createPaymentRequest) {
//        log.info("createPayment 서비스 진입");
//        log.info("createPaymentRequestId : {}", createPaymentRequest.getOrderNum());
        OrderResponse orderResponse = orderClient.getOrderDetails(createPaymentRequest.getOrderNum());
        if(orderResponse.getStatus() != OrderStatus.IN_PROGRESS) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        double chance = Math.random();
        if (chance < 0.2) {
            orderClient.failedByCustomer(createPaymentRequest.getOrderNum());
            throw new CustomException(ErrorCode.FAILED_CUSTOMER);
        }

        OrderResponse completeOrderResponse = orderClient.completeOrder(createPaymentRequest.getOrderNum());

        Payment payment = Payment.create(completeOrderResponse.getOrderNum(), completeOrderResponse.getBuyerNum(), completeOrderResponse.getQuantity(), completeOrderResponse.getPrice());

//        log.info("payment 생성 : {}", payment);
//        log.info("payment id : {}", payment.getOrderNum());

        Payment newPayment = paymentRepository.save(payment);

//        log.info("payment 저장 : {}", newPayment);

        return new PaymentDetailsResponse(newPayment.getPaymentNum(), newPayment.getOrderNum(), newPayment.getBuyerNum(), newPayment.getQuantity(), newPayment.getPrice(), newPayment.getCreatedAt());
    }


    @Transactional
    public void delete(Long paymentNum) {
        Payment targetPayment = paymentRepository.findById(paymentNum)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        if(targetPayment.getDeletedAt() != null) {
            throw new CustomException(ErrorCode.DELETED_PAYMENT);
        }

        orderClient.cancelOrder(targetPayment.getOrderNum());

        targetPayment.delete();
    }
}
