package com.habitpay.habitpay.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class CustomJwtException extends RuntimeException {

    private final CustomJwtErrorResponse customJwtErrorResponse;

    public CustomJwtException(CustomJwtErrorResponse customJwtErrorResponse) {
        super((customJwtErrorResponse.getErrorMessage()));
        this.customJwtErrorResponse = customJwtErrorResponse;
    }
}
