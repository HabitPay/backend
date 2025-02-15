package com.habitpay.habitpay.domain.challengeabsencefee.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberFeeView {
    private Long memberId;
    private String nickname;
    private int totalFee;
    private int completionRate;

    public MemberFeeView(Long memberId, String nickname, int totalFee, double completionRate) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.totalFee = totalFee;
        this.completionRate = (int) Math.round(completionRate);
    }
}
