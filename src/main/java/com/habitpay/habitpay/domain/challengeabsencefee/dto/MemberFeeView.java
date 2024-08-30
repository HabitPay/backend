package com.habitpay.habitpay.domain.challengeabsencefee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MemberFeeView {
    private String nickname;
    private int totalFee;
    private int completionRate;

    public static MemberFeeView of(MemberFeeDTO memberFeeDto, int totalCount) {
        return MemberFeeView.builder()
                .nickname(memberFeeDto.getNickname())
                .totalFee(memberFeeDto.getTotalFee())
                .completionRate(memberFeeDto.getSuccessCount() / totalCount * 100)
                .build();
    }
}
