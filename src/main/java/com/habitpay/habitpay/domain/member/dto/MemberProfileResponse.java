package com.habitpay.habitpay.domain.member.dto;

import com.habitpay.habitpay.domain.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberProfileResponse {
    private String nickname;
    private String imageUrl;

    public static MemberProfileResponse of(Member member, String imageUrl) {
        return MemberProfileResponse.builder()
                .nickname(member.getNickname())
                .imageUrl(imageUrl)
                .build();
    }
}
