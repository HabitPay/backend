package com.habitpay.habitpay.domain.challenge.dto;

import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
public class ChallengeCreationRequest {
    private String title;
    private String description;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private byte participatingDays;
    private int feePerAbsence;
}
