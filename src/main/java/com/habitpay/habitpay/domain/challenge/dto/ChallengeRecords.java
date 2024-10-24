package com.habitpay.habitpay.domain.challenge.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ChallengeRecords {
    private List<LocalDate> successDayList;
    private List<LocalDate> failDayList;
    private List<LocalDate> upcomingDayList;

    public void addSuccessDay(LocalDate date) {
        successDayList.add(date);
    }

    public void addFailDay(LocalDate date) {
        failDayList.add(date);
    }

    public void addUpcomingDay(LocalDate date) {
        upcomingDayList.add(date);
    }
}
