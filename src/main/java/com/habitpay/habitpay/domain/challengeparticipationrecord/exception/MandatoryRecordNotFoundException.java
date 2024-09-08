package com.habitpay.habitpay.domain.challengeparticipationrecord.exception;

import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.global.error.exception.EntityNotFoundException;
import com.habitpay.habitpay.global.error.exception.ErrorCode;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MandatoryRecordNotFoundException extends EntityNotFoundException {

    public MandatoryRecordNotFoundException(Long enrollmentId, String targetDate) {
        super(String.format(
                "ChallengeParticipationRecord for ChallengeEnrollment [id: %d] for [date: %s] is not found",
                enrollmentId, targetDate), ErrorCode.RECORD_NOT_FOUND);
    }
}
