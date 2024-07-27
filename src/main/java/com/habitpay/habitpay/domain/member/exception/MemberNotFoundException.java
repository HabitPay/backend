package com.habitpay.habitpay.domain.member.exception;

import com.habitpay.habitpay.global.error.exception.EntityNotFoundException;

public class MemberNotFoundException extends EntityNotFoundException {

    public MemberNotFoundException(Long id) {
        super(String.format("[id: %d] is not found", id));
    }
}
