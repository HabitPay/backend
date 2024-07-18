package com.habitpay.habitpay.domain.challenge.dto;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
@Builder
public class ChallengeCreationResponse {
    private String hostNickname;
    private Long challengeId;
    private String title;
    private String description;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private int participatingDays;
    private int feePerAbsence;

    public static ChallengeCreationResponse of(Member host, Challenge challenge) {
        return ChallengeCreationResponse.builder()
                .hostNickname(host.getNickname())
                .challengeId(challenge.getId())
                .title(challenge.getTitle())
                .description(challenge.getDescription())
                .startDate(challenge.getStartDate())
                .endDate(challenge.getEndDate())
                .participatingDays(challenge.getParticipatingDays())
                .feePerAbsence(challenge.getFeePerAbsence())
                .build();
    }
}
