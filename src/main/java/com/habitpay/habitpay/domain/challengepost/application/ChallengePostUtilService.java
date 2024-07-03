package com.habitpay.habitpay.domain.challengepost.application;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.dao.ChallengeEnrollmentRepository;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengePostUtilService {

    private final ChallengeParticipationRecordCreationService challengeParticipationRecordCreationService;
    private final ChallengeParticipationRecordSearchService challengeParticipationRecordSearchService;

    public void authorizePostWriter(ChallengePost challengePost) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!challengePost.getWriter().getEmail().equals(email)) {
            throw new CustomJwtException(HttpStatus.UNAUTHORIZED, CustomJwtErrorInfo.UNAUTHORIZED, "Not a Member who posted.");
        }
    }

    public boolean isChallengeHost(Challenge challenge, Member member) {
        return challenge.getHost().equals(member);
    }

    public boolean isChallengeHost(Challenge challenge, String email) {
        String hostEmail = challenge.getHost().getEmail();
        return hostEmail.equals(email);
    }

    public void verifyChallengeParticipationRecord(ChallengePost post, ChallengeEnrollment enrollment) {
        Challenge challenge = post.getChallengeEnrollment().getChallenge();

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

        if (alreadyParticipateToday(enrollment, now)) {
            return;
        }

        //todo: challenge.numberOfParticipants +1 하는 메서드 만들기 (challenge 도메인이나 서비스 내 위치?)
        challengeParticipationRecordCreationService.save(enrollment, post);
    }

    private boolean alreadyParticipateToday(ChallengeEnrollment enrollment, ZonedDateTime now) {
        List<ChallengeParticipationRecord> optionalRecordList = challengeParticipationRecordSearchService.findAllByChallengeEnrollment(enrollment)
                .orElse(new ArrayList<>());

        if (optionalRecordList.isEmpty()) { return false; }

        LocalDateTime today = now.toLocalDate().atStartOfDay();

        Optional<ChallengeParticipationRecord> optionalRecord = optionalRecordList
                .stream()
//                .filter(record -> record.getCreatedAt().toLocalDate().isEqual(ChronoLocalDate.from(today)))
                .filter(record -> {
                    log.info("레코드 toLocalDate {}", record.getCreatedAt().toLocalDate());
                    log.info("크로노 today {}", String.valueOf(ChronoLocalDate.from(today)));
                    return record.getCreatedAt().toLocalDate().isEqual(ChronoLocalDate.from(today));
                })
                .findFirst();

        // todo : 디버깅 용도
        log.info("투데이 atStartOfDay {}", today);

        return optionalRecord.isPresent();
    }

}
