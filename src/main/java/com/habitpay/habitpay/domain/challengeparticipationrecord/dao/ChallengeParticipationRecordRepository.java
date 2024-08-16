package com.habitpay.habitpay.domain.challengeparticipationrecord.dao;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengeparticipationrecord.domain.ChallengeParticipationRecord;
import org.hibernate.boot.model.source.spi.AttributePath;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChallengeParticipationRecordRepository extends JpaRepository<ChallengeParticipationRecord, Long> {
    List<ChallengeParticipationRecord> findAllByChallengeEnrollment(ChallengeEnrollment enrollment);

    // todo: 사용처 보고 필요하면 수정
    Optional<ChallengeParticipationRecord> findByChallengeEnrollment(ChallengeEnrollment challengeEnrollment);

    // todo: 생성 일시 말고 타겟 데이트로 찾아야 함
    Optional<ChallengeParticipationRecord> findByChallengeEnrollmentAndCreatedAtBetween(ChallengeEnrollment enrollment, LocalDateTime startOfDay, LocalDateTime endOfDay);

    Optional<ChallengeParticipationRecord> findByChallengeEnrollmentAndTargetDate(ChallengeEnrollment enrollment, LocalDate targetDate);

    @Query("SELECT new com.habitpay.habitpay.domain.challengeparticipationrecord.dto.RecordCheckDTO(r, c.feePerAbsence) " +
            "FROM ChallengeParticipationRecord r " +
            "JOIN FETCH r.participationStat " +
            "JOIN r.challenge c " +
            "WHERE r.challenge IN :challenges " +
            "AND r.targetDate = :targetDate")
    List<ChallengeParticipationRecord> findByChallengeInAndTargetDate(List<Challenge> challenges, LocalDate targetDate);
}
