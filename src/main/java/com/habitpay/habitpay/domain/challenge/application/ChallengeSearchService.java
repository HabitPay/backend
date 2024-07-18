package com.habitpay.habitpay.domain.challenge.application;

import com.habitpay.habitpay.domain.challenge.dao.ChallengeRepository;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ChallengeSearchService {
    private final ChallengeRepository challengeRepository;

    @Transactional(readOnly = true)
    public Challenge getChallengeById(Long id) {
        // TODO: 공통 예외처리 적용하기
        return challengeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하는 챌린지가 아닙니다."));
    }
}
