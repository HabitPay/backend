package com.habitpay.habitpay.domain.challenge.application;

import com.habitpay.habitpay.domain.challenge.dao.ChallengeRepository;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ChallengeSearchService {
    private final ChallengeRepository challengeRepository;

    @Transactional
    public Challenge findById(Long id) {
        Optional<Challenge> optionalChallenge = Optional.ofNullable(challengeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하는 챌린지가 아닙니다.")));

        return optionalChallenge.get();
    }
}
