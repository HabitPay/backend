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
import com.habitpay.habitpay.global.config.timezone.TimeZoneConverter;
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
public class ChallengeSchedulerService {

    private final ChallengeRepository challengeRepository;
    private final SchedulerTaskHelperService schedulerTaskHelperService;

    @Scheduled(cron = "0 * * * * *", zone = "Asia/Seoul")
    public void setChallengeForStart() {
        List<Challenge> challengeList = schedulerTaskHelperService.findStartingChallenges();
        challengeList.forEach(Challenge::setStateInProgress);
        schedulerTaskHelperService.createRecordsForChallenges(challengeList);
        challengeRepository.saveAll(challengeList);
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void checkParticipationForChallenge() {

        ZonedDateTime today = TimeZoneConverter.convertEtcToLocalTimeZone(ZonedDateTime.now());
        ZonedDateTime yesterday = today.minusDays(1).with(LocalTime.MIDNIGHT);
        List<Challenge> challengeList = schedulerTaskHelperService.findChallengesForTodayParticipation(yesterday);

        if (challengeList.isEmpty()) { return; }

        schedulerTaskHelperService.checkFailedParticipation(challengeList, yesterday);
    }

    @Scheduled(cron = "0 * * * * *", zone = "Asia/Seoul")
    public void setChallengeForEnd() {
        List<Challenge> challengeList = schedulerTaskHelperService.findEndingChallenges();
        challengeList.forEach(Challenge::setStateCompletedPendingSettlement);
        challengeRepository.saveAll(challengeList);
    }
}
