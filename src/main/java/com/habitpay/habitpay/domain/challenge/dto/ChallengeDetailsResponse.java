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
    private String hostNickname;
    private List<String> enrolledMembersProfileImageList;
    private Boolean isHost;
    private Boolean isMemberEnrolledInChallenge;

    public static ChallengeDetailsResponse of(Member member, Challenge challenge, List<String> enrolledMembersProfileImageList, Boolean isMemberEnrolledInChallenge) {
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
                // todo : 챌린지 엔티티에 전체 벌금 필드 살린 후 수정하기
                .totalAbsenceFee(0)
//                .totalAbsenceFee(challenge.getTotalAbsenceFee)
                .hostNickname(challenge.getHost().getNickname())
                .enrolledMembersProfileImageList(enrolledMembersProfileImageList)
                .isHost(challenge.getHost().getId().equals(member.getId()))
                .isMemberEnrolledInChallenge(isMemberEnrolledInChallenge)
                .build();
    }
}
