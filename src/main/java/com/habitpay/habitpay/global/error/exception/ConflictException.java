package com.habitpay.habitpay.global.error.exception;

public class ConflictException extends BusinessException {

    public ConflictException(String message) {
        super(message, ErrorCode.CONFLICT);
    }

    public ConflictException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ConflictException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
