package com.habitpay.habitpay.global.error.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Common
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "Entity Not Found");

    private HttpStatus status;
    private final String message;

}
