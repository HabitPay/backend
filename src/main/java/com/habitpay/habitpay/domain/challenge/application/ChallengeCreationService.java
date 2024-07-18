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

import java.time.ZonedDateTime;

@Service
@AllArgsConstructor
public class ChallengeCreationService {
    private final MemberSearchService memberSearchService;
    private final ChallengeRepository challengeRepository;

    public SuccessResponse<ChallengeCreationResponse> createChallenge(ChallengeCreationRequest challengeCreationRequest, Long id) {
        Member host = memberSearchService.getMemberById(id);
        if (isStartDateBeforeNow(challengeCreationRequest.getStartDate())) {
            // TODO: 예외 처리 공통 응답 적용하기
            throw new IllegalArgumentException("챌린지 시작 시간은 현재 시간 이후만 가능합니다.");
        }

        Challenge newChallenge = Challenge.of(host, challengeCreationRequest);
        challengeRepository.save(newChallenge);

        return SuccessResponse.of(
                "챌린지가 생성되었습니다.",
                ChallengeCreationResponse.of(host, newChallenge)
        );
    }

    private boolean isStartDateBeforeNow(ZonedDateTime startDate) {
        return startDate.isBefore(ZonedDateTime.now());
    }
}
