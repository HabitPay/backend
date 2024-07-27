package com.habitpay.habitpay.domain.member.exception;

import com.habitpay.habitpay.global.error.exception.ErrorCode;
import com.habitpay.habitpay.global.error.exception.InvalidValueException;

public class InvalidNicknameException extends InvalidValueException {

    public InvalidNicknameException(String nickname, ErrorCode errorCode) {
        super(String.format("[nickname: %s] is invalid", nickname), errorCode);
    }
}
