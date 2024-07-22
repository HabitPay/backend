package com.habitpay.habitpay.domain.challenge.dto;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
@Builder
public class ChallengeDetailsResponse {
    private String title;
    private String description;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private ZonedDateTime stopDate;
    private int numberOfParticipants;
    private int participatingDays;
    private int feePerAbsence;
    private Boolean isPaidAll;
    private String hostNickname;
    private String hostProfileImage;
    private Boolean isHost;
    private Boolean isMemberEnrolledInChallenge;

    public static ChallengeDetailsResponse of(Member member, Challenge challenge, Boolean isMemberEnrolledInChallenge) {
        return ChallengeDetailsResponse.builder()
                .title(challenge.getTitle())
                .description(challenge.getDescription())
                .startDate(challenge.getStartDate())
                .endDate(challenge.getEndDate())
                .stopDate(challenge.getStopDate())
                .numberOfParticipants(challenge.getNumberOfParticipants())
                .isPaidAll(challenge.isPaidAll())
                .participatingDays(challenge.getParticipatingDays())
                .feePerAbsence(challenge.getFeePerAbsence())
                .hostNickname(challenge.getHost().getNickname())
                .hostProfileImage(challenge.getHost().getImageFileName())
                .isHost(challenge.getHost().getId().equals(member.getId()))
                .isMemberEnrolledInChallenge(isMemberEnrolledInChallenge)
                .build();
    }
}
