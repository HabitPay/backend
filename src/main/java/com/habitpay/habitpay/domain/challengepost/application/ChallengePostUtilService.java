package com.habitpay.habitpay.domain.challengepost.application;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challenge.domain.ChallengeState;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengeparticipationrecord.application.ChallengeParticipationRecordSearchService;
import com.habitpay.habitpay.domain.challengeparticipationrecord.application.ChallengeParticipationRecordUpdateService;
import com.habitpay.habitpay.domain.challengeparticipationrecord.domain.ChallengeParticipationRecord;
import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.challengepost.exception.InvalidStateForPostException;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.global.error.exception.ErrorCode;
import com.habitpay.habitpay.global.error.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

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

    public void checkChallengePeriodForPost(Challenge challenge) {
        ZonedDateTime now = ZonedDateTime.now();
        ChallengeState state = challenge.getState();

        if (now.isAfter(challenge.getStartDate()) && state.equals(ChallengeState.SCHEDULED)) {
            throw new InvalidStateForPostException(challenge.getId());
        }

        if (!(state.equals(ChallengeState.IN_PROGRESS)
                || state.equals(ChallengeState.COMPLETED_PENDING_SETTLEMENT))
                || !now.isAfter(challenge.getStartDate())) {
            throw new ForbiddenException(ErrorCode.POST_EDITABLE_ONLY_WITHIN_CHALLENGE_PERIOD);
        }
    }

    public void verifyChallengePostForRecord(ChallengePost post) {
        ChallengeEnrollment enrollment = post.getChallengeEnrollment();
        Challenge challenge = enrollment.getChallenge();
        ZonedDateTime now = ZonedDateTime.now();

        if (!now.isAfter(challenge.getStartDate()) || !now.isBefore(challenge.getEndDate())) {
            return;
        }

        ZonedDateTime nowInLocal = now.withZoneSameInstant(ZoneId.of("Asia/Seoul"));

        DayOfWeek nowDayOfWeek = nowInLocal.getDayOfWeek();
        int nowDayOfWeekValue = nowDayOfWeek.getValue();
        boolean todayIsParticipationDay = (challenge.getParticipatingDays() & (1 << (7 - nowDayOfWeekValue))) != 0;

        if (!todayIsParticipationDay) {
            return;
        }

        ZonedDateTime nowDate = nowInLocal.with(LocalTime.MIDNIGHT);
        ChallengeParticipationRecord record = challengeParticipationRecordSearchService
                .findByChallengeEnrollmentAndTargetDate(enrollment, nowDate);
        if (record.existsChallengePost()) {
            return;
        }

        challengeParticipationRecordUpdateService.setChallengePost(record, post);
    }

}
