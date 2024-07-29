package com.habitpay.habitpay.global.error.exception;

public class UnauthorizedException extends BusinessException {

    public UnauthorizedException(String message) {
        super(message, ErrorCode.UNAUTHORIZED);
    }

    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UnauthorizedException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
