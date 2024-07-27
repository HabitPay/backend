package com.habitpay.habitpay.global.error.exception;

public class ForbiddenException extends BusinessException {

    public ForbiddenException(String message) {
        super(message, ErrorCode.FORBIDDEN);
    }

    public ForbiddenException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ForbiddenException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
