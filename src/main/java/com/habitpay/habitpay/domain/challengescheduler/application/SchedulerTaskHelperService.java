package com.habitpay.habitpay.domain.challengescheduler.application;

import com.habitpay.habitpay.domain.challenge.application.ChallengeUtilService;
import com.habitpay.habitpay.domain.challenge.dao.ChallengeRepository;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challenge.domain.ChallengeState;
import com.habitpay.habitpay.domain.challengeenrollment.dao.ChallengeEnrollmentRepository;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengeparticipationrecord.application.ChallengeParticipationRecordSearchService;
import com.habitpay.habitpay.domain.challengeparticipationrecord.dao.ChallengeParticipationRecordRepository;
import com.habitpay.habitpay.domain.challengeparticipationrecord.domain.ChallengeParticipationRecord;
import com.habitpay.habitpay.domain.participationstat.domain.ParticipationStat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SchedulerTaskHelperService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeEnrollmentRepository challengeEnrollmentRepository;
    private final ChallengeParticipationRecordRepository challengeParticipationRecordRepository;
    private final ChallengeParticipationRecordSearchService challengeParticipationRecordSearchService;
    private final ChallengeUtilService challengeUtilService;

    public List<Challenge> findStartingChallenges() {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime startOfDay = ZonedDateTime.now(zoneId).toLocalDate().atStartOfDay(zoneId);
        ZonedDateTime endOfDay = ZonedDateTime.now(zoneId).toLocalDate().atTime(LocalTime.MAX).atZone(zoneId);

        return challengeRepository.findAllByStartDateBetweenAndState(startOfDay, endOfDay, ChallengeState.SCHEDULED);
    }

    public void createRecordsForChallenges(List<Challenge> challengeList) {
        challengeList.forEach(challenge -> {
            List<ChallengeEnrollment> enrollmentList = challengeEnrollmentRepository.findAllByChallenge(challenge);
            List<ZonedDateTime> participationDates = challengeUtilService.getParticipationDates(challenge);

            List<ChallengeParticipationRecord> recordList = createRecordsForEnrollments(enrollmentList, participationDates);
            challengeParticipationRecordRepository.saveAll(recordList);
        });
    }

    public List<ChallengeParticipationRecord> createRecordsForEnrollments(
            List<ChallengeEnrollment> enrollmentList,
            List<ZonedDateTime> participationDates) {

        List<ChallengeParticipationRecord> recordList = new ArrayList<>();

        enrollmentList.forEach(enrollment -> {
            participationDates.forEach(date -> {
                ZonedDateTime startOfDate = date.with(LocalTime.MIDNIGHT);
                recordList.add(ChallengeParticipationRecord.of(enrollment, startOfDate));
            });
        });

        return recordList;
    }

    public List<Challenge> findChallengesForTodayParticipation(ZonedDateTime targetDay) {
        DayOfWeek yesterdayOfWeek = targetDay.getDayOfWeek();
        byte yesterdayBitPosition = (byte) ((byte) 1 << (7 - yesterdayOfWeek.getValue()));

        return challengeRepository.findAllByStateAndParticipatingDays(ChallengeState.IN_PROGRESS, yesterdayBitPosition);
    }

    public void checkFailedParticipation(List<Challenge> challengeList,
                                         ZonedDateTime yesterday,
                                         List<ParticipationStat> failStatList,
                                         List<ChallengeParticipationRecord> failRecordList) {
        challengeParticipationRecordSearchService.findByChallengesAndTargetDate(challengeList, yesterday)
                .forEach(record -> {
                    if (!record.existChallengePost()) {
                        ParticipationStat stat = record.getParticipationStat();
                        stat.setFailureCount(stat.getFailureCount() + 1);
                        stat.setTotalFee(stat.getTotalFee() + record.getChallenge().getFeePerAbsence());
                        failStatList.add(record.getParticipationStat());
                        failRecordList.add(record);
                    }
                });
    }

    public List<Challenge> findEndingChallenges(ZonedDateTime targetDay) {
        ZonedDateTime startOfDay = targetDay.with(LocalTime.MIDNIGHT);
        ZonedDateTime endOfDay = targetDay.with(LocalTime.MAX);

        return challengeRepository.findAllByEndDateBetweenAndState(startOfDay, endOfDay, ChallengeState.IN_PROGRESS);
    }
}
