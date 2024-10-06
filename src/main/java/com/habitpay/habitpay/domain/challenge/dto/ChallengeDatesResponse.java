package com.habitpay.habitpay.domain.challenge.dto;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
@Builder
public class ChallengeDatesResponse {

    private ZonedDateTime startDate;
    private ZonedDateTime endDate;

    public static ChallengeDatesResponse from(Challenge challenge) {
        return ChallengeDatesResponse.builder()
                .startDate(challenge.getStartDate())
                .endDate(challenge.getEndDate())
                .build();
    }
}
