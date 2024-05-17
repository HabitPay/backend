package com.habitpay.habitpay.domain.challenge.application;

import com.habitpay.habitpay.domain.challenge.dao.ChallengeRepository;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ChallengeCreateService {
    private final ChallengeRepository challengeRepository;

    @Transactional
    public void save(Challenge challenge) {
        challengeRepository.save(challenge);
    }
}
