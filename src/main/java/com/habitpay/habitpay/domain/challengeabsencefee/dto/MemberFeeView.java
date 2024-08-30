package com.habitpay.habitpay.domain.challengeabsencefee.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberFeeView {
    private String nickname;
    private int totalFee;
    private int completionRate;

    public MemberFeeView(String nickname, int totalFee, int completionRate) {
        this.nickname = nickname;
        this.totalFee = totalFee;
        this.completionRate = completionRate;
    }
}
