package com.habitpay.habitpay.domain.challengePost.dao;

import com.habitpay.habitpay.domain.challengePost.domain.ChallengePost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChallengePostRepository extends JpaRepository<ChallengePost, Long> {
    Optional<ChallengePost> findByChallengeEnrollmentId(Long challengeEnrollmentId);
}
