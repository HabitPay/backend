package com.habitpay.habitpay.domain.challengescheduler.application;

import com.habitpay.habitpay.domain.challenge.dao.ChallengeRepository;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challenge.domain.ChallengeState;
import com.habitpay.habitpay.domain.challengeenrollment.dao.ChallengeEnrollmentRepository;
import com.habitpay.habitpay.domain.challengeparticipationrecord.application.ChallengeParticipationRecordSearchService;
import com.habitpay.habitpay.domain.challengeparticipationrecord.dao.ChallengeParticipationRecordRepository;
import com.habitpay.habitpay.domain.challengeparticipationrecord.domain.ChallengeParticipationRecord;
import com.habitpay.habitpay.domain.participationstat.dao.ParticipationStatRepository;
import com.habitpay.habitpay.domain.participationstat.domain.ParticipationStat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeSchedulerService {

    private final ChallengeRepository challengeRepository;
    private final ParticipationStatRepository participationStatRepository;
    private final ChallengeParticipationRecordRepository challengeParticipationRecordRepository;
    private final SchedulerTaskHelperService schedulerTaskHelperService;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void setChallengeForStart() {

        ZonedDateTime startTime = ZonedDateTime.now();
        ZoneId timeZone = startTime.getZone();
        log.info("setChallengeForStart started at : {}. TimeZone: {}", startTime, timeZone);

        // todo : 시작 날짜 ZonedDateTime으로 받고 있음 -> 날짜 데이터(without 시간)만으로 데이터 타입 바꾸기(프론트, 백 모두)
        List<Challenge> challengeList = schedulerTaskHelperService.findStartingChallenges();

        challengeList.forEach(Challenge::setStateInProgress);

        schedulerTaskHelperService.createRecordsForChallenges(challengeList);
        challengeRepository.saveAll(challengeList);

        ZonedDateTime endTime = ZonedDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        log.info("setChallengeForStart completed at : {}. Execution time: {}ms", endTime, duration.toMillis());
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void checkParticipationForChallenge() {

        ZonedDateTime startTime = ZonedDateTime.now();
        ZoneId timeZone = startTime.getZone();
        log.info("checkParticipationForChallenge started at : {}. TimeZone: {}", startTime, timeZone);

        ZonedDateTime yesterday = ZonedDateTime.now().minusDays(1).with(LocalTime.MIDNIGHT);
        List<Challenge> challengeList = schedulerTaskHelperService.findChallengesForTodayParticipation(yesterday);

        if (challengeList.isEmpty()) { return; }

        List<ParticipationStat> failStatList = new ArrayList<>();
        List<ChallengeParticipationRecord> failRecordList = new ArrayList<>();
        schedulerTaskHelperService.checkFailedParticipation(challengeList, yesterday, failStatList, failRecordList);

        participationStatRepository.saveAll(failStatList);
        challengeParticipationRecordRepository.deleteAll(failRecordList);

        ZonedDateTime endTime = ZonedDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        log.info("checkParticipationForChallenge completed at : {}. Execution time: {}ms", endTime, duration.toMillis());
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void setChallengeForEnd() {

        ZonedDateTime startTime = ZonedDateTime.now();
        ZoneId timeZone = startTime.getZone();
        log.info("setChallengeForEnd started at : {}. TimeZone: {}", startTime, timeZone);

        ZonedDateTime yesterday = ZonedDateTime.now().minusDays(1);
        List<Challenge> challengeList = schedulerTaskHelperService.findEndingChallenges(yesterday);
        challengeList.forEach(Challenge::setStateCompletedPendingSettlement);

        ZonedDateTime endTime = ZonedDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        log.info("setChallengeForEnd completed at : {}. Execution time: {}ms", endTime, duration.toMillis());
    }
}
