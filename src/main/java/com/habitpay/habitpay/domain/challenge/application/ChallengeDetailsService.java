package com.habitpay.habitpay.domain.challenge.application;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challenge.dto.ChallengeDetailsResponse;
import com.habitpay.habitpay.domain.challengeenrollment.dao.ChallengeEnrollmentRepository;
import com.habitpay.habitpay.domain.member.application.MemberSearchService;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeDetailsService {
    private final ChallengeSearchService challengeSearchService;
    private final ChallengeEnrollmentRepository challengeEnrollmentRepository;

    public SuccessResponse<ChallengeDetailsResponse> getChallengeDetails(Long challengeId, Member member) {
        Challenge challenge = challengeSearchService.getChallengeById(challengeId);
        Boolean isMemberEnrolledInChallenge = challengeEnrollmentRepository.findByMemberAndChallenge(member, challenge)
                .isPresent();

        return SuccessResponse.of(
                "",
                ChallengeDetailsResponse.of(
                        member,
                        challenge,
                        isMemberEnrolledInChallenge)
        );
    }
}
