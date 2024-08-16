package com.habitpay.habitpay.domain.challengeabsencefee.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberFeeDTO {
    private String nickname;
    private int totalFee;
    private int successCount;

    public static MemberFeeDTO of(String nickname, int totalFee, int successCount) {
        return MemberFeeDTO.builder()
                .nickname(nickname)
                .totalFee(totalFee)
                .successCount(successCount)
                .build();
    }
}
