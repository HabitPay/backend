package com.habitpay.habitpay.domain.challengeenrollment.dao;

import com.habitpay.habitpay.domain.challengeabsencefee.dto.MemberFeeView;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChallengeEnrollmentRepository extends JpaRepository<ChallengeEnrollment, Long> {
    Optional<ChallengeEnrollment> findByMember(Member member);

    Optional<ChallengeEnrollment> findByMemberAndChallenge(Member member, Challenge challenge);

    List<ChallengeEnrollment> findAllByMember(Member member);
    List<ChallengeEnrollment> findTop3ByChallenge(Challenge challenge);
    List<ChallengeEnrollment> findAllByChallenge(Challenge challenge);

    @Query("SELECT e.member " +
    "FROM ChallengeEnrollment e " +
    "WHERE e.challenge = :challenge")
    List<Member> findAllMemberByChallenge(Challenge challenge);

    @Query("SELECT new com.habitpay.habitpay.domain.challengeabsencefee.dto.MemberFeeView(m.id, m.nickname, s.totalFee, (s.successCount * 1.0 / :totalCount) * 100) " +
    "FROM ChallengeEnrollment e " +
    "JOIN e.member m " +
    "JOIN e.participationStat s " +
    "WHERE e.challenge = :challenge")
    List<MemberFeeView> findMemberFeeViewByChallenge(Challenge challenge, int totalCount);
}