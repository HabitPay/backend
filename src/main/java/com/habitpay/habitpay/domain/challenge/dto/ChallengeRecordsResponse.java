package com.habitpay.habitpay.domain.challenge.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Builder
public class ChallengeRecordsResponse {
    List<ZonedDateTime> successDayList;
    List<ZonedDateTime> failDayList;
    List<ZonedDateTime> upcomingDayList;
}
