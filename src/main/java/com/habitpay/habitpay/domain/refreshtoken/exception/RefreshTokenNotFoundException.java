package com.habitpay.habitpay.domain.refreshtoken.exception;

import com.habitpay.habitpay.global.error.exception.EntityNotFoundException;
import com.habitpay.habitpay.global.error.exception.ErrorCode;

public class RefreshTokenNotFoundException extends EntityNotFoundException {

    public RefreshTokenNotFoundException(String refreshToken) {
        super(String.format("RefreshToken [%s] is not found", refreshToken), ErrorCode.JWT_REFRESH_TOKEN_NOT_FOUND);
    }
}
