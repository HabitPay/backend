package com.habitpay.habitpay.global.handler;

import com.habitpay.habitpay.global.error.ErrorResponse;
import com.habitpay.habitpay.global.error.exception.BusinessException;
import com.habitpay.habitpay.global.error.exception.ErrorCode;
import com.habitpay.habitpay.global.handler.dto.ExceptionResponse;
import com.habitpay.habitpay.domain.refreshtoken.exception.CustomJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<String> error(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(final BusinessException ex) {
        log.error("handleBusinessException: ", ex);
        final ErrorCode errorCode = ex.getErrorCode();
        final ErrorResponse response = ErrorResponse.of(errorCode);
        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }
}
