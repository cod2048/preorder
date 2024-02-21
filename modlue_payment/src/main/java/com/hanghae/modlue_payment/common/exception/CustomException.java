package com.hanghae.modlue_payment.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException{
    private final ErrorCode errorCode;
    private final String message;
    private final HttpStatus Status;

    public CustomException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
        this.Status = errorCode.getStatus();
    }
}