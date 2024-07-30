package com.habitpay.habitpay.domain.challengepost.dao;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChallengePostRepository extends JpaRepository<ChallengePost, Long> {
    Slice<ChallengePost> findAllByChallengeEnrollment(ChallengeEnrollment enrollment, Pageable pageable);
    Slice<ChallengePost> findAllByChallengeId(Long challengeId, Pageable pageable);
    Slice<ChallengePost> findAllByChallengeIdAndIsAnnouncementTrue(Long challengeId, Pageable pageable);
}
