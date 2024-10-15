package com.habitpay.habitpay.domain.challenge.application;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.global.config.timezone.TimeZoneConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ChallengeUtilService {

    TimeZoneConverter timeZoneConverter;

    public boolean isTodayParticipatingDay(Challenge challenge) {
        ZonedDateTime nowInLocal = timeZoneConverter.convertEtcToLocalTimeZone(ZonedDateTime.now());
        DayOfWeek today = nowInLocal.getDayOfWeek();
        int todayBitPosition = 6 - (today.getValue() - 1);

        return (challenge.getParticipatingDays() & (1 << todayBitPosition)) != 0;
    }

    public List<ZonedDateTime> getParticipationDates(Challenge challenge) {

        List<ZonedDateTime> dates = new ArrayList<>();

        byte daysOfWeek = challenge.getParticipatingDays();
        for (int i = 0; i < 7; ++i) {
            if ((daysOfWeek & (1 << i)) != 0) {

                DayOfWeek targetDay = DayOfWeek.of(7 - i);
                ZonedDateTime startDateInLocal = timeZoneConverter.convertEtcToLocalTimeZone(challenge.getStartDate());
                ZonedDateTime targetDate = startDateInLocal.with(TemporalAdjusters.nextOrSame(targetDay));

                while (!targetDate.isAfter(challenge.getEndDate())) {
                    dates.add(targetDate);
                    targetDate = targetDate.plusWeeks(1);
                }
            }
        }

        return dates;
    }
}
