package com.habitpay.habitpay.domain.challenge.domain;

import lombok.Getter;

@Getter
public enum ChallengeState {
    SCHEDULED,
    IN_PROGRESS,
    COMPLETED_PENDING_SETTLEMENT,
    COMPLETED_SETTLED,
    CANCELED,
    CANCELED_SETTLED,
    CANCELED_BEFORE_START;
}