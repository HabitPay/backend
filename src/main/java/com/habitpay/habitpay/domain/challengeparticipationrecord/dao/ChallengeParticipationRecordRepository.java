package com.habitpay.habitpay.domain.challengeparticipationrecord.dao;

import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengeparticipationrecord.domain.ChallengeParticipationRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChallengeParticipationRecordRepository extends JpaRepository<ChallengeParticipationRecord, Long> {
    Optional<List<ChallengeParticipationRecord>> findAllByChallengeEnrollment(ChallengeEnrollment enrollment);

    Optional<ChallengeParticipationRecord> findByChallengeEnrollment(ChallengeEnrollment challengeEnrollment);

    Optional<ChallengeParticipationRecord> findByChallengeEnrollmentAndCreatedAtBetween(ChallengeEnrollment enrollment, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
