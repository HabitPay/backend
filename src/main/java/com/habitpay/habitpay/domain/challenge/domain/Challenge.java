package com.habitpay.habitpay.domain.challenge.domain;

import com.habitpay.habitpay.domain.challenge.dto.ChallengeCreationRequest;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.model.BaseTime;
import com.habitpay.habitpay.global.config.timezone.TimeZoneConverter;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Slf4j
@Table(name = "challenge")
public class Challenge extends BaseTime {

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.REMOVE)
    List<ChallengeEnrollment> challengeEnrollmentList = new ArrayList<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member host;
    @Column(nullable = false)
    private String title;

    @Column()
    private String description;

    @Column(nullable = false)
    private ChallengeState state;

    @Column(nullable = false)
    private ZonedDateTime startDate;

    @Column(nullable = false)
    private ZonedDateTime endDate;

    @Column()
    private ZonedDateTime stopDate;

    @Column(nullable = false)
    private int numberOfParticipants;

    @Column(nullable = false)
    private byte participatingDays;

    @Column(nullable = false)
    private int totalParticipatingDaysCount;

    @Column(nullable = false)
    private int feePerAbsence;

    @Column(nullable = false)
    private int totalAbsenceFee;

    @Column(nullable = false)
    private boolean isPaidAll;

    @Builder
    public Challenge(Member member, String title, String description, ZonedDateTime startDate, ZonedDateTime endDate,
                     byte participatingDays, int totalParticipatingDaysCount, int feePerAbsence) {
        this.host = member;
        this.title = title;
        this.description = description;
        this.state = ChallengeState.SCHEDULED;
        this.startDate = startDate;
        this.endDate = endDate;
        this.participatingDays = participatingDays;
        this.totalParticipatingDaysCount = totalParticipatingDaysCount;
        this.feePerAbsence = feePerAbsence;
    }

    public static Challenge of(Member host, ChallengeCreationRequest challengeCreationRequest) {
        return Challenge.builder()
                .member(host)
                .title(challengeCreationRequest.getTitle())
                .description(challengeCreationRequest.getDescription())
                .startDate(challengeCreationRequest.getStartDate())
                .endDate(challengeCreationRequest.getEndDate())
                .participatingDays(challengeCreationRequest.getParticipatingDays())
                .totalParticipatingDaysCount(calculateTotalParticipatingDays(challengeCreationRequest))
                .feePerAbsence(challengeCreationRequest.getFeePerAbsence())
                .build();
    }

    private static int calculateTotalParticipatingDays(ChallengeCreationRequest challengeCreationRequest) {
        int count = 0;
        EnumSet<DayOfWeek> daysOfParticipatingDays = EnumSet.noneOf(DayOfWeek.class);

        // 1. 챌린지 참여 요일 저장
        for (int bit = 0; bit <= 6; bit += 1) {
            int todayBitPosition = 6 - bit;
            if ((challengeCreationRequest.getParticipatingDays() & (1 << todayBitPosition)) != 0) {
                daysOfParticipatingDays.add(DayOfWeek.of(bit + 1));
            }
        }

        // 2. 챌린지 총 참여 일수 계산
        ZonedDateTime date = challengeCreationRequest.getStartDate();
        ZonedDateTime endDate = challengeCreationRequest.getEndDate();
        while (date.isBefore(endDate)) {
            if (daysOfParticipatingDays.contains(date.getDayOfWeek())) {
                count += 1;
            }
            date = date.plusDays(1);
        }

        return count;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setNumberOfParticipants(int numberOfParticipants) {
        this.numberOfParticipants = numberOfParticipants;
    }

    public void setStateInProgress() {
        this.state = ChallengeState.IN_PROGRESS;
    }

    public void setStateCompletedPendingSettlement() {
        this.state = ChallengeState.COMPLETED_PENDING_SETTLEMENT;
    }

    public boolean isTodayParticipatingDay() {
        ZonedDateTime nowInLocal = TimeZoneConverter.convertEtcToLocalTimeZone(ZonedDateTime.now());
        DayOfWeek today = nowInLocal.getDayOfWeek();
        int todayBitPosition = 6 - (today.getValue() - 1);

        return (this.getParticipatingDays() & (1 << todayBitPosition)) != 0;
    }

    public List<ZonedDateTime> getParticipationDates() {

        List<ZonedDateTime> dates = new ArrayList<>();

        byte daysOfWeek = this.getParticipatingDays();
        for (int i = 0; i < 7; ++i) {
            if ((daysOfWeek & (1 << i)) != 0) {

                DayOfWeek targetDay = DayOfWeek.of(7 - i);
                ZonedDateTime startDateInLocal = TimeZoneConverter.convertEtcToLocalTimeZone(this.getStartDate());
                ZonedDateTime targetDate = startDateInLocal.with(TemporalAdjusters.nextOrSame(targetDay));

                // todo
                ZonedDateTime tempEndDate = TimeZoneConverter.convertEtcToLocalTimeZone(this.getEndDate());
                ZonedDateTime endDate = tempEndDate.toLocalDate().atTime(LocalTime.MAX).atZone(tempEndDate.getZone());

                while (!targetDate.isAfter(endDate)) {
                    dates.add(targetDate);
                    targetDate = targetDate.plusWeeks(1);
                }
            }
        }

        return dates;
    }

}