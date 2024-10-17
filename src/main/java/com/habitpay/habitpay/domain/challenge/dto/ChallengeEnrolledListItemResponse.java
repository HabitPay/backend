package com.habitpay.habitpay.domain.challenge.dto;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.participationstat.domain.ParticipationStat;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
@Builder
public class ChallengeEnrolledListItemResponse {
    private Long challengeId;
    private String title;
    private String description;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private ZonedDateTime stopDate;
    private int totalParticipatingDaysCount;
    private int numberOfParticipants;
    private int participatingDays;
    private int totalFee;
    private Boolean isPaidAll;
    private String hostProfileImage;
    private Boolean isMemberGivenUp;
    private int successCount;
    private Boolean isTodayParticipatingDay;
    private Boolean isParticipatedToday;

    public static ChallengeEnrolledListItemResponse of(Challenge challenge, ChallengeEnrollment challengeEnrollment, ParticipationStat stat, String hostProfileImage, boolean isTodayParticipatingDay, boolean isParticipatedToday) {
        return ChallengeEnrolledListItemResponse.builder()
                .challengeId(challenge.getId())
                .title(challenge.getTitle())
                .description(challenge.getDescription())
                .startDate(challenge.getStartDate())
                .endDate(challenge.getEndDate())
                .stopDate(challenge.getStopDate())
                .totalParticipatingDaysCount(challenge.getTotalParticipatingDaysCount())
                .numberOfParticipants(challenge.getNumberOfParticipants())
                .isPaidAll(challenge.isPaidAll())
                .participatingDays(challenge.getParticipatingDays())
                .totalFee(stat.getTotalFee())
                .hostProfileImage(hostProfileImage)
                .isMemberGivenUp(challengeEnrollment.isGivenUp())
                .successCount(stat.getSuccessCount())
                .isTodayParticipatingDay(isTodayParticipatingDay)
                .isParticipatedToday(isParticipatedToday)
                .build();
    }
}
