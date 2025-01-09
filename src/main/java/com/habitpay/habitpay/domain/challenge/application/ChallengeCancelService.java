package com.habitpay.habitpay.domain.challenge.application;

import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.global.response.SuccessCode;
import com.habitpay.habitpay.global.response.SuccessResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ChallengeCancelService {

    public SuccessResponse<Void> cancelChallengeAfterStart(Long challengeId, Member member) {
        return SuccessResponse.of(
                SuccessCode.CANCEL_CHALLENGE_AFTER_START_SUCCESS
        );
    }
}
