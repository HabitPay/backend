package com.habitpay.habitpay.domain.challenge.application;

import com.habitpay.habitpay.domain.challenge.dao.ChallengeRepository;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challenge.dto.ChallengeCreationRequest;
import com.habitpay.habitpay.domain.challenge.dto.ChallengeCreationResponse;
import com.habitpay.habitpay.domain.member.application.MemberSearchService;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.global.response.SuccessResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ChallengeCreationService {
    private final MemberSearchService memberSearchService;
    private final ChallengeRepository challengeRepository;

    public SuccessResponse<ChallengeCreationResponse> createChallenge(ChallengeCreationRequest challengeCreationRequest, Long id) {
        Member host = memberSearchService.getMemberById(id);
        Challenge newChallenge = Challenge.of(host, challengeCreationRequest);
        challengeRepository.save(newChallenge);

        return SuccessResponse.of(
                "챌린지가 생성되었습니다.",
                ChallengeCreationResponse.of(host, newChallenge)
        );
    }
}
