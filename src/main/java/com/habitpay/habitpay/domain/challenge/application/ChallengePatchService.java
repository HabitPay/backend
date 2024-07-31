package com.habitpay.habitpay.domain.challenge.application;

import com.habitpay.habitpay.domain.challenge.dao.ChallengeRepository;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challenge.dto.ChallengePatchRequest;
import com.habitpay.habitpay.domain.challenge.dto.ChallengePatchResponse;
import com.habitpay.habitpay.domain.member.application.MemberSearchService;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.global.error.exception.ErrorCode;
import com.habitpay.habitpay.global.error.exception.ForbiddenException;
import com.habitpay.habitpay.global.error.exception.InvalidValueException;
import com.habitpay.habitpay.global.response.SuccessCode;
import com.habitpay.habitpay.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChallengePatchService {
    private final ChallengeSearchService challengeSearchService;

    @Transactional
    public SuccessResponse<ChallengePatchResponse> patch(Long challengeId, ChallengePatchRequest challengePatchRequest, Member member) {
        Challenge challenge = challengeSearchService.getChallengeById(challengeId);

        if (isChallengeHost(member, challenge) == false) {
            throw new ForbiddenException(ErrorCode.ONLY_HOST_CAN_MODIFY);
        }

        if (isChallengeDescriptionUnchanged(challenge, challengePatchRequest)) {
            throw new InvalidValueException(ErrorCode.DUPLICATED_CHALLENGE_DESCRIPTION);
        }

        challenge.setDescription(challengePatchRequest.getDescription());

        return SuccessResponse.of(
                SuccessCode.PATCH_CHALLENGE_SUCCESS,
                ChallengePatchResponse.of(challenge)
        );
    }

    private boolean isChallengeHost(Member member, Challenge challenge) {
        return member.getId().equals(challenge.getHost().getId());
    }

    private boolean isChallengeDescriptionUnchanged(Challenge challenge, ChallengePatchRequest challengePatchRequest) {
        return challenge.getDescription().equals(challengePatchRequest.getDescription());
    }
}
