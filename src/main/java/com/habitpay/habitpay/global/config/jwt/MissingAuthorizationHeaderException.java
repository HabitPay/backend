package com.habitpay.habitpay.global.config.jwt;

import org.springframework.security.core.AuthenticationException;

public class MissingAuthorizationHeaderException extends AuthenticationException {
    public MissingAuthorizationHeaderException(String message) {
        super(message);
    }
}
