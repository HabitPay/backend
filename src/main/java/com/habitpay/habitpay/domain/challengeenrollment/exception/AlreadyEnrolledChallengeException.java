package com.habitpay.habitpay.domain.challengeenrollment.exception;

import com.habitpay.habitpay.global.error.exception.ConflictException;
import com.habitpay.habitpay.global.error.exception.ErrorCode;

public class AlreadyEnrolledChallengeException extends ConflictException {

    public AlreadyEnrolledChallengeException(Long memberId, Long challengeId) {
        super(
                String.format("[Member: %d] is already enrolled in [Challenge: %d]", memberId, challengeId),
                ErrorCode.ALREADY_ENROLLED_IN_CHALLENGE
        );
    }
}
