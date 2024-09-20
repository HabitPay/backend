package com.habitpay.habitpay.domain.challenge.dao;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challenge.domain.ChallengeState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    @Query(value = "SELECT * FROM challenge WHERE state = :state AND participating_days & :day = :day", nativeQuery = true)
    List<Challenge> findAllByStateAndParticipatingDays(
            @Param("state") ChallengeState state,
            @Param("day") byte participationDays);

    Page<Challenge> findAll(Pageable pageable);
}
