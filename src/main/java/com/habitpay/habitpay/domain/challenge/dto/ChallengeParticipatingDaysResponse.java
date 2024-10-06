package com.habitpay.habitpay.domain.challenge.dto;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import lombok.Builder;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.EnumSet;
import java.util.Locale;

@Getter
@Builder
public class ChallengeParticipatingDaysResponse {

    private String[] participatingDays;

    public static ChallengeParticipatingDaysResponse from(Challenge challenge) {
        return ChallengeParticipatingDaysResponse.builder()
                .participatingDays(extractDaysFromBits(challenge.getParticipatingDays()))
                .build();
    }

    public static String[] extractDaysFromBits(int participatingDays) {
        EnumSet<DayOfWeek> daysOfParticipatingDays = EnumSet.noneOf(DayOfWeek.class);

        for (int bit = 0; bit <= 6; bit += 1) {
            int todayBitPosition = 6 - bit;
            if ((participatingDays & (1 << todayBitPosition)) != 0) {
                daysOfParticipatingDays.add(DayOfWeek.of(bit + 1));
            }
        }

        return daysOfParticipatingDays.stream()
                .map(day -> day.getDisplayName(TextStyle.SHORT, Locale.KOREAN))
                .toArray(String[]::new);
    }
}
