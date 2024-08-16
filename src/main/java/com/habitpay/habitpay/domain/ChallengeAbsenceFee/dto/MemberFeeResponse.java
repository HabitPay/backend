package com.habitpay.habitpay.domain.ChallengeAbsenceFee.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberFeeResponse {
    private String nickname;
    private int totalFee;
    private int completionRate;

    public static MemberFeeResponse of(MemberFeeDTO memberFeeDto, int totalCount) {
        return MemberFeeResponse.builder()
                .nickname(memberFeeDto.getNickname())
                .totalFee(memberFeeDto.getTotalFee())
                .completionRate(memberFeeDto.getSuccessCount() / totalCount * 100)
                .build();
    }
}
