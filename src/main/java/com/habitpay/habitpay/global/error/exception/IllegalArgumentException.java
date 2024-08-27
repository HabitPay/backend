package com.habitpay.habitpay.global.error.exception;

public class IllegalArgumentException extends BusinessException {

    public IllegalArgumentException(String message) {
        super(message, ErrorCode.ENTITY_NOT_FOUND);
    }

    public IllegalArgumentException(ErrorCode errorCode) {
        super(errorCode);
    }

    public IllegalArgumentException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
