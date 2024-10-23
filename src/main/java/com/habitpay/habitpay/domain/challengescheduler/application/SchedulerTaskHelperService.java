package com.habitpay.habitpay.domain.challengescheduler.application;

import com.habitpay.habitpay.domain.challenge.dao.ChallengeRepository;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challenge.domain.ChallengeState;
import com.habitpay.habitpay.domain.challengeenrollment.dao.ChallengeEnrollmentRepository;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengeparticipationrecord.application.ChallengeParticipationRecordSearchService;
import com.habitpay.habitpay.domain.challengeparticipationrecord.dao.ChallengeParticipationRecordRepository;
import com.habitpay.habitpay.domain.challengeparticipationrecord.domain.ChallengeParticipationRecord;
import com.habitpay.habitpay.domain.participationstat.dao.ParticipationStatRepository;
import com.habitpay.habitpay.domain.participationstat.domain.ParticipationStat;
import com.habitpay.habitpay.global.config.timezone.TimeZoneConverter;
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
    private final ParticipationStatRepository participationStatRepository;
    private final ChallengeParticipationRecordSearchService challengeParticipationRecordSearchService;

    public List<Challenge> findStartingChallenges() {
        ZonedDateTime today = TimeZoneConverter.convertEtcToLocalTimeZone(ZonedDateTime.now());
        ZonedDateTime startOfDay = today.toLocalDate().atStartOfDay(today.getZone());
        ZonedDateTime endOfDay = today.toLocalDate().atTime(LocalTime.MAX).atZone(today.getZone());

        return challengeRepository.findAllByStartDateBetweenAndState(startOfDay, endOfDay, ChallengeState.SCHEDULED);
    }

    public void createRecordsForChallenges(List<Challenge> challengeList) {
        challengeList.forEach(challenge -> {
            List<ChallengeEnrollment> enrollmentList = challengeEnrollmentRepository.findAllByChallenge(challenge);
            List<ZonedDateTime> participationDates = challenge.getParticipationDates();

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

    public void checkFailedParticipation(List<Challenge> challengeList, ZonedDateTime yesterday) {
        List<Challenge> feeAddedChallengeList = new ArrayList<>();
        List<ParticipationStat> failStatList = new ArrayList<>();
        List<ChallengeParticipationRecord> failRecordList = new ArrayList<>();

        challengeParticipationRecordSearchService.findByChallengesAndTargetDate(challengeList, yesterday)
                .forEach(record -> {
                    if (!record.existsChallengePost()) {
                        ParticipationStat stat = record.getParticipationStat();
                        Challenge challenge = record.getChallenge();
                        stat.setFailureCount(stat.getFailureCount() + 1);
                        stat.setTotalFee(stat.getTotalFee() + challenge.getFeePerAbsence());
                        challenge.setTotalAbsenceFee(challenge.getTotalAbsenceFee() + challenge.getFeePerAbsence());
                        failStatList.add(record.getParticipationStat());
                        failRecordList.add(record);
                        saveOrUpdateChallengeList(feeAddedChallengeList, challenge);
                    }
                });

        participationStatRepository.saveAll(failStatList);
        challengeParticipationRecordRepository.deleteAll(failRecordList);
        challengeRepository.saveAll(feeAddedChallengeList);
    }

    private void saveOrUpdateChallengeList(List<Challenge> challengeList, Challenge challenge) {
        int index = challengeList.indexOf(challenge);
        if (index != -1) {
            challengeList.set(index, challenge);
        } else {
            challengeList.add(challenge);
        }
    }

    public List<Challenge> findEndingChallenges(ZonedDateTime targetDay) {
        ZonedDateTime startOfDay = targetDay.with(LocalTime.MIDNIGHT);
        ZonedDateTime endOfDay = targetDay.with(LocalTime.MAX);

        return challengeRepository.findAllByEndDateBetweenAndState(startOfDay, endOfDay, ChallengeState.IN_PROGRESS);
    }
}
