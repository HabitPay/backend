package com.habitpay.habitpay.domain.challengepost.dao;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChallengePostRepository extends JpaRepository<ChallengePost, Long> {
    List<ChallengePost> findAllByChallengeEnrollmentId(Long challengeEnrollmentId);
}
