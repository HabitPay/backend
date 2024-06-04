package com.habitpay.habitpay.domain.challengePost.dao;

import com.habitpay.habitpay.domain.challengePost.domain.ChallengePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChallengePostRepository extends JpaRepository<ChallengePost, Long> {
    Optional<List<ChallengePost>> findAllByChallengeEnrollmentId(Long challengeEnrollmentId);
}
