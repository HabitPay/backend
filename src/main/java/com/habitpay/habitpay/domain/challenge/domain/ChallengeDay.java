package com.habitpay.habitpay.domain.challenge.domain;

import lombok.Getter;

@Getter
public enum ChallengeDay {
    MONDAY(0b01000000),
    TUESDAY(0b00100000),
    WEDNESDAY(0b00010000),
    THURSDAY(0b00001000),
    FRIDAY(0b00000100),
    SATURDAY(0b00000010),
    SUNDAY(0b00000001);

    private final byte bitValue;

    ChallengeDay(int bitValue) {
        this.bitValue = (byte) bitValue;
    }
}
