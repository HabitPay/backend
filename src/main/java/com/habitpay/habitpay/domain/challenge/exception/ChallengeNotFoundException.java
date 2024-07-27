package com.habitpay.habitpay.domain.challenge.exception;

import com.habitpay.habitpay.global.error.exception.EntityNotFoundException;
import com.habitpay.habitpay.global.error.exception.ErrorCode;

public class ChallengeNotFoundException extends EntityNotFoundException {

    public ChallengeNotFoundException(Long id) {
        super(String.format("Challenge [id: %d] is not found", id), ErrorCode.CHALLENGE_NOT_FOUND);
    }
}
