package com.habitpay.habitpay.global.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalAccessException.class)
    protected String illegalAccessExceptionHandler(IllegalAccessException exception) {
        return "@RestControllerAdvice GlobalExceptionHandler IllegalAccessException let's go";
    }
}
