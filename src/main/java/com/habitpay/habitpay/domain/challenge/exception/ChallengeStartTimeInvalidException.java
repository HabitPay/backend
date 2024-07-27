package com.habitpay.habitpay.domain.challenge.exception;

import com.habitpay.habitpay.global.error.exception.ErrorCode;
import com.habitpay.habitpay.global.error.exception.InvalidValueException;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ChallengeStartTimeInvalidException extends InvalidValueException {

    public ChallengeStartTimeInvalidException(ZonedDateTime dateTime) {
        super(toGeneralDateTimeFormat(dateTime), ErrorCode.CHALLENGE_START_TIME_INVALID);
    }

    public static String toGeneralDateTimeFormat(ZonedDateTime value) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        return value.format(formatter);
    }
}
