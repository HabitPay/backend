package com.habitpay.habitpay.domain.challengeparticipationrecord.application;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengeparticipationrecord.dao.ChallengeParticipationRecordRepository;
import com.habitpay.habitpay.domain.challengeparticipationrecord.domain.ChallengeParticipationRecord;
import com.habitpay.habitpay.domain.challengeparticipationrecord.exception.MandatoryRecordNotFoundException;
import com.habitpay.habitpay.domain.challengeparticipationrecord.exception.RecordNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeParticipationRecordSearchService {

    private final ChallengeParticipationRecordRepository challengeParticipationRecordRepository;

    public ChallengeParticipationRecord findById(Long id) {
        return challengeParticipationRecordRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(id));
    }

    public List<ChallengeParticipationRecord> findAllByChallengeEnrollment(ChallengeEnrollment enrollment) {
        return challengeParticipationRecordRepository.findAllByChallengeEnrollment(enrollment);
    }

    public boolean hasParticipationRecord(ChallengeEnrollment challengeEnrollment) {
        ZonedDateTime startOfToday = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("Asia/Seoul")).with(LocalTime.MIDNIGHT);

        return challengeParticipationRecordRepository.findByChallengeEnrollmentAndTargetDate(challengeEnrollment, startOfToday)
                .map(ChallengeParticipationRecord::existChallengePost)
                .orElseGet(() -> false);
    }

    public ChallengeParticipationRecord findByChallengeEnrollmentAndTargetDate(
            ChallengeEnrollment enrollment,
            ZonedDateTime startOfTargetDate) {
        return challengeParticipationRecordRepository.findByChallengeEnrollmentAndTargetDate(enrollment, startOfTargetDate)
                .orElseThrow(() -> new MandatoryRecordNotFoundException(enrollment.getId(), startOfTargetDate.toString()));
    }

    public List<ChallengeParticipationRecord> findByChallengesAndTargetDate(
            List<Challenge> challengeList,
            ZonedDateTime startOfTargetDate) {
        return challengeParticipationRecordRepository.findByChallengeInAndTargetDate(challengeList, startOfTargetDate);
    }
}
