package com.habitpay.habitpay.domain.challengeenrollment.dao;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChallengeEnrollmentRepository extends JpaRepository<ChallengeEnrollment, Long> {
    Optional<ChallengeEnrollment> findByMember(Member member);

    Optional<ChallengeEnrollment> findByMemberAndChallenge(Member member, Challenge challenge);

    List<ChallengeEnrollment> findAllByMember(Member member);
}
