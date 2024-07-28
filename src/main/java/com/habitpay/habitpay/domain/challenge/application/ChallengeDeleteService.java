package com.habitpay.habitpay.domain.challenge.application;

import com.habitpay.habitpay.domain.challenge.dao.ChallengeRepository;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.global.response.SuccessCode;
import com.habitpay.habitpay.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeDeleteService {
    private final ChallengeSearchService challengeSearchService;
    private final ChallengeRepository challengeRepository;

    public SuccessResponse<Void> delete(Long id) {
        Challenge challenge = challengeSearchService.getChallengeById(id);
        challengeRepository.delete(challenge);

        return SuccessResponse.of(SuccessCode.DELETE_CHALLENGE_SUCCESS);
    }
}
