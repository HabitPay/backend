package com.habitpay.habitpay.domain.challenge.exception;

import com.habitpay.habitpay.global.error.exception.ErrorCode;
import com.habitpay.habitpay.global.error.exception.InvalidValueException;

public class InvalidChallengeParticipatingDaysException extends InvalidValueException {

    public InvalidChallengeParticipatingDaysException() {
        super(ErrorCode.INVALID_CHALLENGE_PARTICIPATING_DAYS);
    }

}
