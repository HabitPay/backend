package com.habitpay.habitpay.domain.challengeenrollment.exception;

import com.habitpay.habitpay.global.error.exception.EntityNotFoundException;
import com.habitpay.habitpay.global.error.exception.ErrorCode;

public class NotEnrolledChallengeException extends EntityNotFoundException {

    public NotEnrolledChallengeException(Long memberId, Long challengeId) {
        super(
                String.format("[Member: %d] is already enrolled in [Challenge: %d]", memberId, challengeId),
                ErrorCode.NOT_ENROLLED_IN_CHALLENGE
        );
    }
}
