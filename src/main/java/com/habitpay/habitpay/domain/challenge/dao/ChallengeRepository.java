package com.habitpay.habitpay.domain.challenge.dao;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    
}
