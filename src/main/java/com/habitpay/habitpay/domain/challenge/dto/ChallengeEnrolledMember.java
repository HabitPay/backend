package com.habitpay.habitpay.domain.challenge.dto;

import com.habitpay.habitpay.domain.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChallengeEnrolledMember {
    private Long id;
    private String nickname;
    private String profileImageUrl;

    public static ChallengeEnrolledMember of(Long id, String nickname, String imageUrl) {
        return ChallengeEnrolledMember.builder()
                .id(id)
                .nickname(nickname)
                .profileImageUrl(imageUrl)
                .build();
    }
}
