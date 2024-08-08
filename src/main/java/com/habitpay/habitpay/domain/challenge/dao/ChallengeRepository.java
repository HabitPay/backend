package com.habitpay.habitpay.domain.challenge.dao;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    List<Challenge> findAllByStateAndParticipatingDays(byte state, byte participationDays);
}
