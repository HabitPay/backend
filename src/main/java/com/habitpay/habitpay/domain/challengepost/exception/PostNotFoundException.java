package com.habitpay.habitpay.domain.challengepost.exception;

import com.habitpay.habitpay.global.error.exception.EntityNotFoundException;
import com.habitpay.habitpay.global.error.exception.ErrorCode;

public class PostNotFoundException extends EntityNotFoundException {

    public PostNotFoundException(Long id) {
        super(String.format("ChallengePost [id: %d] is not found", id), ErrorCode.POST_NOT_FOUND);
    }
}
