package com.habitpay.habitpay.domain.challengepost.application;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengeparticipationrecord.application.ChallengeParticipationRecordUpdateService;
import com.habitpay.habitpay.domain.challengeparticipationrecord.application.ChallengeParticipationRecordSearchService;
import com.habitpay.habitpay.domain.challengeparticipationrecord.domain.ChallengeParticipationRecord;
import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.global.error.exception.ErrorCode;
import com.habitpay.habitpay.global.error.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengePostUtilService {

    private final ChallengeParticipationRecordUpdateService challengeParticipationRecordUpdateService;
    private final ChallengeParticipationRecordSearchService challengeParticipationRecordSearchService;


    public void authorizePostWriter(ChallengePost post, Member member) {
        if (!post.getWriter().equals(member)) {
            throw new ForbiddenException(ErrorCode.JWT_FORBIDDEN_TO_MODIFY_OTHERS_POST);
        }
    }

    public boolean isChallengeHost(Challenge challenge, Member member) {
        return challenge.getHost().equals(member);
    }

    public boolean isChallengePeriodForPost(Challenge challenge) {
        ZonedDateTime now = ZonedDateTime.now();

        return now.isAfter(challenge.getStartDate()) && !challenge.isPaidAll();
    }

    public void verifyChallengePostForRecord(ChallengePost post) {
        ChallengeEnrollment enrollment = post.getChallengeEnrollment();
        Challenge challenge = enrollment.getChallenge();
        ZonedDateTime now = ZonedDateTime.now();

        if (!now.isAfter(challenge.getStartDate()) || !now.isBefore(challenge.getEndDate())) {
            return;
        }

        DayOfWeek nowDayOfWeek = now.getDayOfWeek();
        int nowDayOfWeekValue = nowDayOfWeek.getValue();
        boolean todayIsParticipationDay = (challenge.getParticipatingDays() & (1 << (7 - nowDayOfWeekValue))) != 0;

        if (!todayIsParticipationDay) {
            return;
        }

        if (isAlreadyParticipateToday(enrollment, now)) {
            return;
        }

        LocalDate today = now.toLocalDate();
        challengeParticipationRecordUpdateService.setChallengePost(enrollment, today, post);
    }

    private boolean isAlreadyParticipateToday(ChallengeEnrollment enrollment, ZonedDateTime now) {

        Optional<ChallengeParticipationRecord> optionalRecord
                = challengeParticipationRecordSearchService.findTodayRecordInEnrollment(
                enrollment,
                now.toLocalDate().atStartOfDay(),
                now.toLocalDate().atTime(LocalTime.MAX)
        );

        return optionalRecord.isPresent();
    }

}
