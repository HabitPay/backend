package com.habitpay.habitpay.domain.challenge.application;

import com.habitpay.habitpay.domain.challenge.dao.ChallengeRepository;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challenge.domain.ChallengeState;
import com.habitpay.habitpay.domain.member.application.MemberUtilsService;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.global.config.timezone.TimeZoneConverter;
import com.habitpay.habitpay.global.error.exception.BadRequestException;
import com.habitpay.habitpay.global.error.exception.ErrorCode;
import com.habitpay.habitpay.global.response.SuccessCode;
import com.habitpay.habitpay.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class ChallengeSettlementService {

    private final ChallengeSearchService challengeSearchService;
    private final MemberUtilsService memberUtilsService;
    private final ChallengeRepository challengeRepository;

    public SuccessResponse<Void> settleChallenge(Long challengeId, Member member) {
        Challenge challenge = challengeSearchService.getChallengeById(challengeId);

        memberUtilsService.isChallengeHost(challenge, member);

        ZonedDateTime now = TimeZoneConverter.convertEtcToLocalTimeZone(ZonedDateTime.now());
        if (!now.isAfter(challenge.getEndDate())) {
            throw new BadRequestException(ErrorCode.INVALID_CHALLENGE_SETTLEMENT_TIME);
        }

        switch (challenge.getState()) {
            case COMPLETED_PENDING_SETTLEMENT -> challenge.setStateCompletedSettled();
            case COMPLETED_SETTLED -> challenge.setStateCompletedPendingSettlement();
            case CANCELED -> challenge.setStateCanceledSettled();
            case CANCELED_SETTLED -> challenge.setStateCanceled();
            default -> throw new BadRequestException(ErrorCode.INVALID_CHALLENGE_STATE_FOR_SETTLEMENT);
        }

        challengeRepository.save(challenge);

        return SuccessResponse.of(SuccessCode.CHALLENGE_SETTLEMENT_SUCCESS);
    }
}
