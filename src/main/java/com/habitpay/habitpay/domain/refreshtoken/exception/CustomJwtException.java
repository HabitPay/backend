package com.habitpay.habitpay.domain.refreshToken.exception;

import com.habitpay.habitpay.global.error.CustomJwtErrorInfo;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomJwtException extends RuntimeException {

    private final HttpStatus statusCode;
    private final CustomJwtErrorInfo customJwtErrorInfo;
    private final String message;

    public CustomJwtException(HttpStatus statusCode, CustomJwtErrorInfo customJwtErrorInfo, String Message) {
        super((customJwtErrorInfo.getMessage()));
        this.statusCode = statusCode;
        this.customJwtErrorInfo = customJwtErrorInfo;
        this.message = Message;
    }
}
