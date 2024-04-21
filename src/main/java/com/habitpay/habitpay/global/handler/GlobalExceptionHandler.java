package com.habitpay.habitpay.global.handler;

import com.habitpay.habitpay.global.exception.JWT.ErrorTokenResponse;
import com.habitpay.habitpay.global.exception.JWT.CustomJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomJwtException.class)
    protected ResponseEntity<ErrorTokenResponse> customJwtExceptionError(CustomJwtException exception) {
        return ResponseEntity.status(exception.getStatusCode())
                .body(new ErrorTokenResponse(
                        exception.getCustomJwtErrorInfo().getErrorMessage(),
                        exception.getErrorMessage()
                ));
    }

    // todo : getMessage() 숨기기?
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<String> error(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }
}
