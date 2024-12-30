package com.habitpay.habitpay.global.handler;

import com.habitpay.habitpay.global.error.ErrorResponse;
import com.habitpay.habitpay.global.error.exception.BusinessException;
import com.habitpay.habitpay.global.error.exception.ErrorCode;
import com.habitpay.habitpay.global.error.exception.ForbiddenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(final BusinessException ex) {
        log.error("handleBusinessException: ", ex);
        final ErrorCode errorCode = ex.getErrorCode();
        final ErrorResponse response = ErrorResponse.of(errorCode);
        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    @ExceptionHandler(ForbiddenException.class)
    protected ResponseEntity<ErrorResponse> handleForbiddenException(final ForbiddenException ex) {
        log.error("handleForbiddenException: ", ex);
        final ErrorCode errorCode = ex.getErrorCode();
        final ErrorResponse response = ErrorResponse.of(errorCode);
        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
        MethodArgumentNotValidException ex) {
        final ErrorResponse response = ErrorResponse.of("BAD_REQUEST",
            ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<String> error(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }
}
