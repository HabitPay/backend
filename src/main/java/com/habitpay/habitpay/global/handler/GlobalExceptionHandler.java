package com.habitpay.habitpay.global.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalAccessException.class)
    protected ResponseEntity<String> illegalAccessExceptionError(IllegalAccessException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exception.getMessage());
    }

    // todo
    @ExceptionHandler(Exception.class)
    protected String error(Exception exception) {
        return "error : " + exception.getMessage();
    }
}
