package com.habitpay.habitpay.domain.member.dto;

import com.habitpay.habitpay.domain.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberActivationResponse {
    private String nickname;
    private String accessToken;
    private Long expiresIn;
    private String tokenType;

    // TODO: token 전용 클래스 만들기
    public static MemberActivationResponse of(Member member, String accessToken, Long expiresIn, String tokenType) {
        return MemberActivationResponse.builder()
                .nickname(member.getNickname())
                .accessToken(accessToken)
                .expiresIn(expiresIn)
                .tokenType(tokenType)
                .build();
    }
}
