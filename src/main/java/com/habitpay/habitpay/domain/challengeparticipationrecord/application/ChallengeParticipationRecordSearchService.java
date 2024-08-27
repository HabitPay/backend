package com.habitpay.habitpay.domain.challengeparticipationrecord.application;

import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengeparticipationrecord.dao.ChallengeParticipationRecordRepository;
import com.habitpay.habitpay.domain.challengeparticipationrecord.domain.ChallengeParticipationRecord;
import com.habitpay.habitpay.domain.challengeparticipationrecord.exception.MandatoryRecordNotFoundException;
import com.habitpay.habitpay.domain.challengeparticipationrecord.exception.RecordNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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

    public Optional<ChallengeParticipationRecord> findTodayRecordInEnrollment(
            ChallengeEnrollment enrollment,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay) {
        return challengeParticipationRecordRepository.findByChallengeEnrollmentAndCreatedAtBetween(
                enrollment,
                startOfDay,
                endOfDay
        );
    }

    public ChallengeParticipationRecord findByChallengeEnrollmentAndTargetDate(
            ChallengeEnrollment enrollment,
            LocalDate targetDate) {
        return challengeParticipationRecordRepository.findByChallengeEnrollmentAndTargetDate(enrollment, targetDate)
                .orElseThrow(() -> new MandatoryRecordNotFoundException(enrollment.getId(), targetDate.toString()));
    }
}
