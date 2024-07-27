package com.habitpay.habitpay.domain.challenge.application;

import com.habitpay.habitpay.domain.challenge.dao.ChallengeRepository;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challenge.dto.ChallengePatchRequest;
import com.habitpay.habitpay.domain.challenge.dto.ChallengePatchResponse;
import com.habitpay.habitpay.domain.member.application.MemberSearchService;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChallengePatchService {
    private final MemberSearchService memberSearchService;
    private final ChallengeRepository challengeRepository;
    private final ChallengeSearchService challengeSearchService;

    @Transactional
    public SuccessResponse<ChallengePatchResponse> patch(Long challengeId, ChallengePatchRequest challengePatchRequest, Member member) {
        Challenge challenge = challengeSearchService.getChallengeById(challengeId);

        if (isChallengeHost(member, challenge) == false) {
            // TODO: 공통 예외 처리 응답 적용하기
            throw new IllegalArgumentException("챌린지 주최자만 수정 가능합니다.");
        }

        if (isChallengeDescriptionUnchanged(challenge, challengePatchRequest)) {
            // TODO: 공통 예외 처리 응답 적용하기
            throw new IllegalArgumentException("변경 사항이 없습니다.");
        }

        challenge.patch(challengePatchRequest);
        challengeRepository.save(challenge);

        return SuccessResponse.of(
                "챌린지 정보 수정이 반영되었습니다.",
                ChallengePatchResponse.of(challenge)
        );
    }

    private boolean isChallengeHost(Member member, Challenge challenge) {
        return member.getEmail().equals(challenge.getHost().getEmail());
    }

    private boolean isChallengeDescriptionUnchanged(Challenge challenge, ChallengePatchRequest challengePatchRequest) {
        return challenge.getDescription().equals(challengePatchRequest.getDescription());
    }
}
