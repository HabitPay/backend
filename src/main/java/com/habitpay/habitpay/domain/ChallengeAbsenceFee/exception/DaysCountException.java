package com.habitpay.habitpay.domain.ChallengeAbsenceFee.exception;

import com.habitpay.habitpay.global.error.exception.ErrorCode;
import com.habitpay.habitpay.global.error.exception.IllegalArgumentException;

public class DaysCountException extends IllegalArgumentException {

    public DaysCountException(Long count) {
        super(String.format("[%d] is illegal argument for TotalParticipatingDaysCount", count),
                ErrorCode.TOTAL_PARTICIPATING_DAYS_COUNT_VALUE_IS_ILLEGAL);
    }
}
