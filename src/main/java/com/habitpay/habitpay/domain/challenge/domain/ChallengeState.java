package com.habitpay.habitpay.domain.challenge.domain;

import lombok.Getter;

@Getter
public enum ChallengeState {
    SCHEDULED(0b00000001),
    IN_PROGRESS(0b00000010),
    COMPLETED_PENDING_SETTLEMENT(0b00000100),
    COMPLETED_SETTLED(0b00001000),
    CANCELED(0b00010000),
    CANCELED_BEFORE_START(0b00100000);
    private final byte bitValue;

    ChallengeState(int bitValue) {
        this.bitValue = (byte) bitValue;
    }

}
