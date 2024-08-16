package com.habitpay.habitpay.domain.ChallengeAbsenceFee.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberFeeDTO {
    private String nickname;
    private Long totalFee;
    private Long successCount;

    public static MemberFeeDTO of(String nickname, Long totalFee, Long successCount) {
        return MemberFeeDTO.builder()
                .nickname(nickname)
                .totalFee(totalFee)
                .successCount(successCount)
                .build();
    }
}
