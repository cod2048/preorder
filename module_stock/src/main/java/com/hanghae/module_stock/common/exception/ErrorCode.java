package com.hanghae.module_stock.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // 4xx
    NOT_FOUND(HttpStatus.BAD_REQUEST, "요청사항을 찾지 못했습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    ITEM_STOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "상품에 대한 재고정보를 찾을 수 없습니다."),

    NOT_ENOUGH_STOCK(HttpStatus.CONFLICT, "재고가 부족합니다."),

    // 5xx
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 문제가 있습니다.");


    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

}
