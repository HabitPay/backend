package com.habitpay.habitpay.domain.challenge.dto;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

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
    private int totalAbsenceFee;
    private Boolean isPaidAll;
    private Boolean isTodayParticipatingDay;
    private Boolean isParticipatedToday;
    private String hostNickname;
    private List<String> enrolledMembersProfileImageList;
    private Boolean isHost;
    private Boolean isMemberEnrolledInChallenge;
    private Boolean isGivenUp;
    private String challengeState;

    public static ChallengeDetailsResponse of(Member member, Challenge challenge,
                                              List<String> enrolledMembersProfileImageList,
                                              Boolean isMemberEnrolledInChallenge, Boolean isParticipatedToday, Boolean isGivenUp) {
        return ChallengeDetailsResponse.builder()
                .title(challenge.getTitle())
                .description(challenge.getDescription())
                .startDate(challenge.getStartDate())
                .endDate(challenge.getEndDate())
                .stopDate(challenge.getStopDate())
                .numberOfParticipants(challenge.getNumberOfParticipants())
                .isPaidAll(challenge.isPaidAll())
                .isTodayParticipatingDay(challenge.isTodayParticipatingDay())
                .isParticipatedToday(isParticipatedToday)
                .participatingDays(challenge.getParticipatingDays())
                .feePerAbsence(challenge.getFeePerAbsence())
                .totalAbsenceFee(challenge.getTotalAbsenceFee())
                .hostNickname(challenge.getHost().getNickname())
                .enrolledMembersProfileImageList(enrolledMembersProfileImageList)
                .isHost(challenge.getHost().getId().equals(member.getId()))
                .isMemberEnrolledInChallenge(isMemberEnrolledInChallenge)
                .isGivenUp(isGivenUp)
                .challengeState(String.valueOf(challenge.getState()))
                .build();
    }
}
