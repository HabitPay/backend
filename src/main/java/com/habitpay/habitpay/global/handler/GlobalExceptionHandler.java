package com.habitpay.habitpay.global.handler;

import com.habitpay.habitpay.global.exception.CustomJwtException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomJwtException.class)
    protected ResponseEntity<String> customJwtExceptionError(CustomJwtException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exception.getMessage());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    protected ResponseEntity<String> expiredJwtExceptionError(ExpiredJwtException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exception.getMessage());
    }

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
