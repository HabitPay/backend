package com.habitpay.habitpay.domain.challenge.dto;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChallengeFeePerAbsenceResponse {

    private int feePerAbsence;

    public static ChallengeFeePerAbsenceResponse from(Challenge challenge) {
        return ChallengeFeePerAbsenceResponse.builder()
                .feePerAbsence(challenge.getFeePerAbsence())
                .build();
    }
}
