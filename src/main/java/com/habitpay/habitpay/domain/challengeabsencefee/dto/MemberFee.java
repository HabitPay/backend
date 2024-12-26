package com.habitpay.habitpay.domain.challengeabsencefee.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberFee {
    private String nickname;
    private int totalFee;
    private int completionRate;
    private Boolean isCurrentUser;

    public static MemberFee of(MemberFeeView memberFeeView, Long memberId) {
        return MemberFee.builder()
                .nickname(memberFeeView.getNickname())
                .totalFee(memberFeeView.getTotalFee())
                .completionRate(memberFeeView.getCompletionRate())
                .isCurrentUser(memberFeeView.getMemberId().equals(memberId))
                .build();
    }

    public static List<MemberFee> of(List<MemberFeeView> memberFeeView, Long memberId) {
        return memberFeeView.stream()
                .map(fee -> MemberFee.of(fee, memberId))
                .toList();
    }
}
