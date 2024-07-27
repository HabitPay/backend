package com.habitpay.habitpay.global.error.exception;

public class BadRequestException extends BusinessException {

    public BadRequestException(String message) {
        super(message, ErrorCode.FORBIDDEN);
    }

    public BadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BadRequestException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
