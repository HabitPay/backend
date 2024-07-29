package com.habitpay.habitpay.domain.postphoto.exception;

import com.habitpay.habitpay.global.error.exception.EntityNotFoundException;
import com.habitpay.habitpay.global.error.exception.ErrorCode;

public class PhotoNotFoundException extends EntityNotFoundException {

    public PhotoNotFoundException(Long id) {
        super(String.format("PostPhoto [id: %d] is not found", id), ErrorCode.PHOTO_NOT_FOUND);
    }
}