package com.habitpay.habitpay.domain.challenge.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class ChallengeRecordsResponse {
    private List<LocalDate> successDayList;
    private List<LocalDate> failureDayList;
    private List<LocalDate> upcomingDayList;
}
