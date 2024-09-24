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
    private final ParticipationStatRepository participationStatRepository;
    private final ChallengeParticipationRecordRepository challengeParticipationRecordRepository;
    private final ChallengeParticipationRecordSearchService challengeParticipationRecordSearchService;
    private final SchedulerTaskHelperService schedulerTaskHelperService;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void setChallengeForStart() {

        // todo : 시작 날짜 ZonedDateTime으로 받고 있음 -> 날짜 데이터(without 시간)만으로 데이터 타입 바꾸기(프론트, 백 모두)
        List<Challenge> challengeList = schedulerTaskHelperService.findStartingChallenges();

        challengeList.forEach(Challenge::setStateInProgress);

        schedulerTaskHelperService.createRecordsForChallenges(challengeList);
        challengeRepository.saveAll(challengeList);

        // todo :
        //  챌린지 '생성일'이 '시작 날짜'와 동일한 경우 참여 목록 따로 생성하는 예외 처리 필요
    }



    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void setChallengeForEnd() {}

    @Transactional
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void checkParticipationForChallenge() {

        ZonedDateTime yesterday = ZonedDateTime.now().minusDays(1);
        List<Challenge> challengeList = schedulerTaskHelperService.findChallengesForTodayParticipation(yesterday);
        if (challengeList.isEmpty()) { return; }

        ZonedDateTime startOfYesterday = yesterday.with(LocalTime.MIDNIGHT);
        List<ParticipationStat> failStatList = new ArrayList<>();
        List<ChallengeParticipationRecord> failRecordList = new ArrayList<>();

        challengeParticipationRecordSearchService.findByChallengesAndTargetDate(challengeList, startOfYesterday)
                .forEach(record -> {
                    if (!record.existChallengePost()) {
                        ParticipationStat stat = record.getParticipationStat();
                        stat.setFailureCount(stat.getFailureCount() + 1);
                        stat.setTotalFee(stat.getTotalFee() + record.getChallenge().getFeePerAbsence());
                        failStatList.add(record.getParticipationStat());
                        failRecordList.add(record);
                    }
                });

        participationStatRepository.saveAll(failStatList);
        challengeParticipationRecordRepository.deleteAll(failRecordList);
    }
}
