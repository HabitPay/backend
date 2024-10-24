package com.habitpay.habitpay.domain.challenge.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ChallengeRecords {
    private List<LocalDate> successDayList;
    private List<LocalDate> failureDayList;
    private List<LocalDate> upcomingDayList;

    public ChallengeRecords() {
        successDayList = new ArrayList<>();
        failureDayList = new ArrayList<>();
        upcomingDayList = new ArrayList<>();
    }

    public void addSuccessDay(LocalDate date) {
        successDayList.add(date);
    }

    public void addFailureDay(LocalDate date) {
        failureDayList.add(date);
    }

    public void addUpcomingDay(LocalDate date) {
        upcomingDayList.add(date);
    }
}
