package com.habitpay.habitpay.domain.challengepost.application;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengeparticipationrecord.application.ChallengeParticipationRecordCreationService;
import com.habitpay.habitpay.domain.challengeparticipationrecord.application.ChallengeParticipationRecordSearchService;
import com.habitpay.habitpay.domain.challengeparticipationrecord.domain.ChallengeParticipationRecord;
import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.refreshtoken.exception.CustomJwtException;
import com.habitpay.habitpay.global.error.CustomJwtErrorInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 5;
    private static final int MAX_SIZE = 100;
    private static final String DEFAULT_SORT = "asc";


    public void authorizePostWriter(ChallengePost post, Member member) {
        if (!post.getWriter().equals(member)) {
            throw new CustomJwtException(HttpStatus.UNAUTHORIZED, CustomJwtErrorInfo.UNAUTHORIZED, "해당 포스트의 작성자가 아닙니다.");
        }
    }

    public boolean isChallengeHost(Challenge challenge, Member member) {
        return challenge.getHost().equals(member);
    }

    public Pageable checkPageable(Pageable pageable) {

        if (pageable.getPageNumber() < 0) {
            return PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE);
        }

        if (pageable.getPageSize() < 1) {
            return PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE);
        } else if (pageable.getPageSize() > MAX_SIZE) {
            return PageRequest.of(0, MAX_SIZE);
        }

        // todo: Sort 예외 처리
        // if (pageable.getSort())

        return pageable;
    }

    public void verifyChallengePostForRecord(ChallengePost post) {
        ChallengeEnrollment enrollment = post.getChallengeEnrollment();
        Challenge challenge = enrollment.getChallenge();
        ZonedDateTime now = ZonedDateTime.now();

        if (!now.isAfter(challenge.getStartDate()) || !now.isBefore(challenge.getEndDate())) {
            return;
        }

        // todo : ParticipationDay 비트 방식 확인하기
        //  (우선 8비트 기준 앞자리부터 월요일이라고 상정함 ex.0b10101000(월수금))
        DayOfWeek nowDayOfWeek = now.getDayOfWeek();
        int nowDayOfWeekValue = nowDayOfWeek.getValue();
//        int nowDayOfWeekValue = now.getDayOfWeek().getValue();
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

        //todo: challengeEnrollment.successCount +1 하는 메서드 만들기 (challenge 도메인이나 서비스 내 위치?)
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
