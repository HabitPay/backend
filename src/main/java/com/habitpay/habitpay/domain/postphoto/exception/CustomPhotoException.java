package com.habitpay.habitpay.domain.postphoto.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomPhotoException extends RuntimeException {

    private final HttpStatus statusCode;
    private final String errorResponse;
    private final String message;

    public CustomPhotoException(HttpStatus statusCode, String errorResponse, String message) {
        super(errorResponse);
        this.statusCode = statusCode;
        this.errorResponse = errorResponse;
        this.message = message;
    }
}
