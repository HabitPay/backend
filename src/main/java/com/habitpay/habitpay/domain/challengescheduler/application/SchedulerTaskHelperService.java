package com.habitpay.habitpay.domain.challengescheduler.application;

import com.habitpay.habitpay.domain.challenge.dao.ChallengeRepository;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challenge.domain.ChallengeState;
import com.habitpay.habitpay.domain.challengeenrollment.dao.ChallengeEnrollmentRepository;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengeparticipationrecord.dao.ChallengeParticipationRecordRepository;
import com.habitpay.habitpay.domain.challengeparticipationrecord.domain.ChallengeParticipationRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public List<Challenge> findStartingChallenges() {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime startOfDay = ZonedDateTime.now(zoneId).toLocalDate().atStartOfDay(zoneId);
        ZonedDateTime endOfDay = ZonedDateTime.now(zoneId).toLocalDate().atTime(LocalTime.MAX).atZone(zoneId);

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
                recordList.add(ChallengeParticipationRecord.of(enrollment, date));
            });
        });

        return recordList;
    }
}
