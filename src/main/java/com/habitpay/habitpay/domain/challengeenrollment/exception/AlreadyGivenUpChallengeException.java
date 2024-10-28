package com.habitpay.habitpay.domain.challengeenrollment.exception;

import com.habitpay.habitpay.global.error.exception.BadRequestException;
import com.habitpay.habitpay.global.error.exception.ErrorCode;

public class AlreadyGivenUpChallengeException extends BadRequestException {

    public AlreadyGivenUpChallengeException(Long memberId, Long challengeId) {
        super(
                String.format("[Member: %d] is already given up [Challenge: %d]", memberId, challengeId),
                ErrorCode.ALREADY_ENROLLED_IN_CHALLENGE
        );
    }
}
