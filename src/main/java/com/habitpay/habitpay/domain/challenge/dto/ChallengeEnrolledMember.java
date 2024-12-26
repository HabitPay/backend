package com.habitpay.habitpay.domain.challenge.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChallengeEnrolledMember {
    private Long memberId;
    private String nickname;
    private String profileImageUrl;
    private Boolean isCurrentUser;

    public static ChallengeEnrolledMember of(Long id, String nickname, String imageUrl, Boolean isCurrentUser) {
        return ChallengeEnrolledMember.builder()
                .memberId(id)
                .nickname(nickname)
                .profileImageUrl(imageUrl)
                .isCurrentUser(isCurrentUser)
                .build();
    }
}
