package com.habitpay.habitpay.domain.challengeabsencefee.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberFeeDTO {
    private String nickname;
    private int totalFee;
    private int successCount;

    public MemberFeeDTO(String nickname, int totalFee, int successCount) {
        this.nickname = nickname;
        this.totalFee = totalFee;
        this.successCount = successCount;
    }
}
