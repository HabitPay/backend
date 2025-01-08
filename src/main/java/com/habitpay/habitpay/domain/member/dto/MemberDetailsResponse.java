package com.habitpay.habitpay.domain.member.dto;

import com.habitpay.habitpay.domain.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberDetailsResponse {
    private Long memberId;
    private String nickname;
    private String imageUrl;
    private Boolean isCurrentUser;

    public static MemberDetailsResponse of(Member member, String imageUrl, Boolean isCurrentUser) {
        return MemberDetailsResponse.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .imageUrl(imageUrl)
                .isCurrentUser(isCurrentUser)
                .build();
    }
}
