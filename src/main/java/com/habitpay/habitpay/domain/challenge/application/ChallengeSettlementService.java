package com.habitpay.habitpay.domain.challenge.application;

import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.global.response.SuccessCode;
import com.habitpay.habitpay.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChallengeSettlementService {

    public SuccessResponse<Void> settleChallenge(Long challengeId, Member member) {
        return SuccessResponse.of(SuccessCode.CHALLENGE_SETTLEMENT_SUCCESS);
    }
}
