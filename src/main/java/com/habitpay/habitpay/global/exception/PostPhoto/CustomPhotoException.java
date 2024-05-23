package com.habitpay.habitpay.global.exception.PostPhoto;

import com.habitpay.habitpay.global.error.ErrorResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomPhotoException extends RuntimeException {

    private final HttpStatus statusCode;
    private final ErrorResponse errorResponse;
    private final String message;

    public CustomPhotoException(HttpStatus statusCode, ErrorResponse errorResponse, String message) {
        super(errorResponse.getMessage());
        this.statusCode = statusCode;
        this.errorResponse = errorResponse;
        this.message = message;
    }
}
