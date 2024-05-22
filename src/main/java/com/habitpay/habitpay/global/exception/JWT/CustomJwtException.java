package com.habitpay.habitpay.global.exception.JWT;

import com.habitpay.habitpay.global.error.CustomJwtErrorInfo;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomJwtException extends RuntimeException {

    private final HttpStatus statusCode;
    private final CustomJwtErrorInfo customJwtErrorInfo;
    private final String errorMessage;

    public CustomJwtException(HttpStatus statusCode, CustomJwtErrorInfo customJwtErrorInfo, String errorMessage) {
        super((customJwtErrorInfo.getErrorMessage()));
        this.statusCode = statusCode;
        this.customJwtErrorInfo = customJwtErrorInfo;
        this.errorMessage = errorMessage;
    }
}
