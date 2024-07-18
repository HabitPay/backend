package com.habitpay.habitpay.domain.challenge.dto;

import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChallengeCreationRequest {
    private String title;
    private String description;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private byte participatingDays;
    private int feePerAbsence;
}
