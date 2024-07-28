package com.habitpay.habitpay.domain.challengeparticipationrecord.exception;

import com.habitpay.habitpay.global.error.exception.EntityNotFoundException;
import com.habitpay.habitpay.global.error.exception.ErrorCode;

public class RecordNotFoundException extends EntityNotFoundException {

    public RecordNotFoundException(Long id) {
        super(String.format("ChallengeParticipationRecord [id: %d] is not found", id), ErrorCode.RECORD_NOT_FOUND);
    }
}
