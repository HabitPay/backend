package com.habitpay.habitpay.domain.challengepost.exception;

import com.habitpay.habitpay.global.error.exception.BadRequestException;
import com.habitpay.habitpay.global.error.exception.ErrorCode;

public class InvalidStateForPostException extends BadRequestException {
    public InvalidStateForPostException(Long id) {
        super(String.format("Challenge [id: %d] is not yet set", id), ErrorCode.NEED_TO_WAIT_FOR_CHALLENGE_SET);
    }
}
