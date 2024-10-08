package com.habitpay.habitpay.domain.challenge.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChallengeTotalAbsenceFeeResponse {

    private int totalAbsenceFee;

    public static ChallengeTotalAbsenceFeeResponse from(int totalAbsenceFee) {
        return ChallengeTotalAbsenceFeeResponse
                .builder()
                .totalAbsenceFee(totalAbsenceFee)
                .build();
    }
}