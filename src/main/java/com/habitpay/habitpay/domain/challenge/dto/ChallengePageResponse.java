package com.habitpay.habitpay.domain.challenge.dto;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import java.time.ZonedDateTime;
import lombok.Builder;

@Builder
public record ChallengePageResponse(
    Long id,
    String title,
    ZonedDateTime startDate,
    ZonedDateTime endDate,
    ZonedDateTime stopDate,
    int numberOfParticipants,
    int participatingDays,
    int totalParticipatingDaysCount,
    Boolean isStarted,
    Boolean isEnded,
    String hostNickname,
    String hostProfileImage
) {

    public static ChallengePageResponse of(Challenge challenge, String hostProfileImage) {
        return ChallengePageResponse.builder()
            .id(challenge.getId())
            .title(challenge.getTitle())
            .startDate(challenge.getStartDate())
            .endDate(challenge.getEndDate())
            .stopDate(challenge.getStopDate())
            .numberOfParticipants(challenge.getNumberOfParticipants())
            .participatingDays(challenge.getParticipatingDays())
            .totalParticipatingDaysCount(challenge.getTotalParticipatingDaysCount())
            .isStarted(ZonedDateTime.now().isAfter(challenge.getStartDate()))
            .isEnded(ZonedDateTime.now().isAfter(challenge.getEndDate()))
            .hostNickname(challenge.getHost().getNickname())
            .hostProfileImage(hostProfileImage)
            .build();
    }
}
