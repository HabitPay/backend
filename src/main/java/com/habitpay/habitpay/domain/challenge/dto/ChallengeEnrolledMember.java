package com.habitpay.habitpay.domain.challenge.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChallengeEnrolledMember {
    private Long id;
    private String nickname;
    private String profileImage;

//    public static ChallengeEnrolledMember of() {
//        return ChallengeEnrolledMember.builder()
//                .id()
//                .nickname()
//                .profileImage();
//    }
}
