package com.habitpay.habitpay.domain.challengeparticipationrecord.dao;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengeparticipationrecord.domain.ChallengeParticipationRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ChallengeParticipationRecordRepository extends JpaRepository<ChallengeParticipationRecord, Long> {
    List<ChallengeParticipationRecord> findAllByChallengeEnrollment(ChallengeEnrollment enrollment);

    // todo: 사용처 보고 필요하면 수정
    Optional<List<ChallengeParticipationRecord>> findByChallengeEnrollment(ChallengeEnrollment challengeEnrollment);

    Optional<ChallengeParticipationRecord> findByChallengeEnrollmentAndTargetDate(ChallengeEnrollment challengeEnrollment, ZonedDateTime startOfTargetDate);

    List<ChallengeParticipationRecord> findByChallengeInAndTargetDate(Collection<Challenge> challenge, ZonedDateTime startOfTargetDate);
}
