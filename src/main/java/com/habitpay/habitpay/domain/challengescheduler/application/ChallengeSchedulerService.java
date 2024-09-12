package com.habitpay.habitpay.domain.challengescheduler.application;

import com.habitpay.habitpay.domain.challenge.dao.ChallengeRepository;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challenge.domain.ChallengeState;
import com.habitpay.habitpay.domain.challengeenrollment.dao.ChallengeEnrollmentRepository;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengeparticipationrecord.application.ChallengeParticipationRecordSearchService;
import com.habitpay.habitpay.domain.challengeparticipationrecord.dao.ChallengeParticipationRecordRepository;
import com.habitpay.habitpay.domain.challengeparticipationrecord.domain.ChallengeParticipationRecord;
import com.habitpay.habitpay.domain.member.domain.Member;
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
    private final ChallengeEnrollmentRepository challengeEnrollmentRepository;
    private final ChallengeParticipationRecordSearchService challengeParticipationRecordSearchService;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void setChallengeForStart() {

        // todo : 시작 날짜 ZonedDateTime으로 받는 거 데이터 타입 확인하기

        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime startOfDay = ZonedDateTime.now(zoneId).toLocalDate().atStartOfDay(zoneId);
        ZonedDateTime endOfDay = ZonedDateTime.now(zoneId).toLocalDate().atTime(LocalTime.MAX).atZone(zoneId);

        List<Challenge> challengeList = challengeRepository.findAllByStartDateBetweenAndState(startOfDay, endOfDay, ChallengeState.SCHEDULED);

        challengeList.forEach(Challenge::setStateInProgress);

        challengeList.forEach(challenge -> {
            List<ChallengeEnrollment> enrollmentList = challengeEnrollmentRepository.findAllByChallenge(challenge);
            List<ZonedDateTime> participationDates = challenge.getParticipationDates();

            List<ChallengeParticipationRecord> recordList = createRecordsForEnrollments(enrollmentList, participationDates);
            challengeParticipationRecordRepository.saveAll(recordList);
        });

        challengeRepository.saveAll(challengeList);

        // todo :
        //  챌린지 '생성일'이 '시작 날짜'와 동일한 경우 참여 목록 따로 생성하는 예외 처리 필요
    }

    public List<ChallengeParticipationRecord> createRecordsForEnrollments(
            List<ChallengeEnrollment> enrollmentList,
            List<ZonedDateTime> participationDates) {

        List<ChallengeParticipationRecord> recordList = new ArrayList<>();

        enrollmentList.forEach(enrollment -> {
            participationDates.forEach(date -> {
                recordList.add(ChallengeParticipationRecord.of(enrollment, date));
            });
        });

        return recordList;
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void setChallengeForEnd() {}

    @Transactional
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void checkParticipationForChallenge() {

        ZonedDateTime yesterday = ZonedDateTime.now().minusDays(1);
        DayOfWeek yesterdayOfWeek = yesterday.getDayOfWeek();
        byte yesterdayBitPosition = (byte) ((byte) 1 << (7 - yesterdayOfWeek.getValue()));

        List<Challenge> challengeList = challengeRepository
                .findAllByStateAndParticipatingDays(ChallengeState.IN_PROGRESS, yesterdayBitPosition);
        if (challengeList.isEmpty()) { return; }

        ZonedDateTime startOfTargetDate = yesterday.with(LocalTime.MIDNIGHT);
        List<ParticipationStat> failStatList = new ArrayList<>();
        List<ChallengeParticipationRecord> failRecordList = new ArrayList<>();

        challengeParticipationRecordSearchService.findByChallengesAndTargetDate(challengeList, startOfTargetDate)
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
