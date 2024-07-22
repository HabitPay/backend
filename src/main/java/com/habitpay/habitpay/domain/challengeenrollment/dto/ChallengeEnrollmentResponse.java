package com.habitpay.habitpay.domain.challengeenrollment.dto;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.member.domain.Member;
import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChallengeEnrollmentResponse {
    Long challengeId;
    Long memberId;
    ZonedDateTime enrolledDate;

    public static ChallengeEnrollmentResponse of(Challenge challenge, ChallengeEnrollment challengeEnrollment, Member member) {
        return ChallengeEnrollmentResponse.builder()
                .challengeId(challenge.getId())
                .memberId(member.getId())
                .enrolledDate(challengeEnrollment.getEnrolledDate())
                .build();
    }
}
