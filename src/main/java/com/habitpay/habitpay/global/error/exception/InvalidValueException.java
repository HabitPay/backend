package com.habitpay.habitpay.global.error.exception;

public class InvalidValueException extends BusinessException {

    public InvalidValueException(String message) {
        super(message, ErrorCode.ENTITY_NOT_FOUND);
    }

    public InvalidValueException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
