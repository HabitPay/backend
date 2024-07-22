package com.habitpay.habitpay.domain.challengeenrollment.application;

import com.habitpay.habitpay.domain.challenge.application.ChallengeSearchService;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.dao.ChallengeEnrollmentRepository;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengeenrollment.dto.ChallengeEnrollmentResponse;
import com.habitpay.habitpay.domain.member.application.MemberSearchService;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeEnrollmentService {
    private final MemberSearchService memberSearchService;
    private final ChallengeEnrollmentRepository challengeEnrollmentRepository;
    private final ChallengeSearchService challengeSearchService;

    public SuccessResponse<ChallengeEnrollmentResponse> enroll(Long challengeId, Long userId) {
        Challenge challenge = challengeSearchService.getChallengeById(challengeId);
        validateChallengeEnrollmentTime(challenge);

        Member member = memberSearchService.getMemberById(userId);
        challengeEnrollmentRepository.findByMemberAndChallenge(member, challenge)
                .ifPresent(entity -> {
                    // TODO: 공통 예외 처리 추가하기
                    throw new IllegalStateException("이미 참여한 챌린지입니다.");
                });

        challenge.setNumberOfParticipants(challenge.getNumberOfParticipants() + 1);

        ChallengeEnrollment challengeEnrollment = ChallengeEnrollment.of(member, challenge);
        challengeEnrollmentRepository.save(challengeEnrollment);
        log.info("챌린지 등록 완료");
        return SuccessResponse.of(
                "챌린지에 정상적으로 등록했습니다.",
                ChallengeEnrollmentResponse.of(
                        challenge, challengeEnrollment, member
                )
        );
    }

    private void validateChallengeEnrollmentTime(Challenge challenge) {
        ZonedDateTime now = ZonedDateTime.now();
        if (now.isAfter(challenge.getStartDate())) {
            // TODO: 공통 예외 처리 추가하기
            throw new IllegalStateException("챌린지 등록 가능 시간이 아닙니다.");
        }
    }
}
