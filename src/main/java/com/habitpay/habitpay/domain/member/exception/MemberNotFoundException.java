package com.habitpay.habitpay.domain.member.exception;

import com.habitpay.habitpay.global.error.exception.EntityNotFoundException;
import com.habitpay.habitpay.global.error.exception.ErrorCode;

public class MemberNotFoundException extends EntityNotFoundException {

    public MemberNotFoundException(Long id) {
        super(String.format("[id: %d] is not found", id), ErrorCode.USER_NOT_FOUND);
    }
}
