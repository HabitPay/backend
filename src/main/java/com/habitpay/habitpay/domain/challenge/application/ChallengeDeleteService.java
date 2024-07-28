package com.habitpay.habitpay.domain.challenge.application;

import com.habitpay.habitpay.domain.challenge.dao.ChallengeRepository;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.global.error.exception.ErrorCode;
import com.habitpay.habitpay.global.error.exception.ForbiddenException;
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

    public SuccessResponse<Void> delete(Long challengeId, Long memberId) {
        Challenge challenge = challengeSearchService.getChallengeById(challengeId);

        if (challenge.getHost().getId().equals(memberId) == false) {
            throw new ForbiddenException(ErrorCode.NOT_ALLOWED_TO_DELETE_CHALLENGE);
        }

        challengeRepository.delete(challenge);

        return SuccessResponse.of(SuccessCode.DELETE_CHALLENGE_SUCCESS);
    }
}
