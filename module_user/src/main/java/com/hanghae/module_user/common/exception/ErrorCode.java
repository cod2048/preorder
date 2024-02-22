package com.hanghae.module_user.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // 4xx
    NOT_FOUND(HttpStatus.BAD_REQUEST, "요청사항을 찾지 못했습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾지 못했습니다."),
    EMAIL_REQUIRED(HttpStatus.BAD_REQUEST,"이메일은 필수입니다."),
    NAME_REQUIRED(HttpStatus.BAD_REQUEST,"이름은 필수입니다."),
    PASSWORD_REQUIRED(HttpStatus.BAD_REQUEST,"비밀번호는 필수입니다."),
    USER_ROLE_REQUIRED(HttpStatus.BAD_REQUEST,"판매자/구매자 구분은 필수입니다."),
    EMAIL_AUTH_CODE_INCORRECT(HttpStatus.UNAUTHORIZED,"이메일 인증 코드가 일치하지 않습니다."),
    DELETED_USER(HttpStatus.NOT_FOUND, "탈퇴한 사용자 입니다."),

    // Email
    ALREADY_EXISTS_EMAIL(HttpStatus.BAD_REQUEST,"이미 가입된 이메일입니다."),
    UNABLE_TO_SEND_EMAIL(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송에 실패했습니다."),

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
