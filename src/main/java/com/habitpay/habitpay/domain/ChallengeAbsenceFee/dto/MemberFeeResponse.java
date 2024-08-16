package com.habitpay.habitpay.domain.ChallengeAbsenceFee.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MemberFeeResponse {
    private String nickname;
    private Long totalFee;
    private Double completionRate;
}
