package com.habitpay.habitpay.domain.challengepost.application;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengeparticipationrecord.application.ChallengeParticipationRecordCreationService;
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
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengePostUtilService {

    private final ChallengeParticipationRecordCreationService challengeParticipationRecordCreationService;
    private final ChallengeParticipationRecordSearchService challengeParticipationRecordSearchService;


    public void authorizePostWriter(ChallengePost post, Member member) {
        if (!post.getWriter().equals(member)) {
            throw new ForbiddenException(ErrorCode.JWT_FORBIDDEN_TO_MODIFY_OTHERS_POST);
        }
    }

    public boolean isChallengeHost(Challenge challenge, Member member) {
        return challenge.getHost().equals(member);
    }

    public void verifyChallengePostForRecord(ChallengePost post) {
        ChallengeEnrollment enrollment = post.getChallengeEnrollment();
        Challenge challenge = enrollment.getChallenge();
        ZonedDateTime now = ZonedDateTime.now();

        if (!now.isAfter(challenge.getStartDate()) || !now.isBefore(challenge.getEndDate())) {
            return;
        }

        // todo : ParticipationDay 비트 방식 확인하기
        DayOfWeek nowDayOfWeek = now.getDayOfWeek();
        int nowDayOfWeekValue = nowDayOfWeek.getValue();
        boolean todayIsParticipationDay = (challenge.getParticipatingDays() & (1 << (nowDayOfWeekValue - 1))) != 0;

        // todo : 디버깅 용도
        log.info("오늘은 " + nowDayOfWeek);
        log.info("비트로 표현하자면 " + nowDayOfWeekValue);
        log.info("챌린지 인증해야 하는 날은? " + challenge.getParticipatingDays());
        log.info("오늘은 챌린지 참여날: " + todayIsParticipationDay);

        if (!todayIsParticipationDay) {
            return;
        }

        if (isAlreadyParticipateToday(enrollment, now)) {
            return;
        }

        challengeParticipationRecordCreationService.save(enrollment, post);
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
