package com.habitpay.habitpay.domain.challengeenrollment.application;

import com.habitpay.habitpay.domain.challenge.application.ChallengeSearchService;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.dao.ChallengeEnrollmentRepository;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengeenrollment.dto.ChallengeEnrollmentResponse;
import com.habitpay.habitpay.domain.challengeenrollment.exception.AlreadyEnrolledChallengeException;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.global.error.exception.BadRequestException;
import com.habitpay.habitpay.global.error.exception.ErrorCode;
import com.habitpay.habitpay.global.response.SuccessCode;
import com.habitpay.habitpay.global.response.SuccessResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeEnrollmentService {
    private final ChallengeEnrollmentRepository challengeEnrollmentRepository;
    private final ChallengeSearchService challengeSearchService;

    @Transactional
    public SuccessResponse<ChallengeEnrollmentResponse> enroll(Long challengeId, Member member) {
        Challenge challenge = challengeSearchService.getChallengeById(challengeId);
        validateChallengeEnrollmentTime(challenge);

        challengeEnrollmentRepository.findByMemberAndChallenge(member, challenge)
                .ifPresent(entity -> {
                    throw new AlreadyEnrolledChallengeException(member.getId(), challengeId);
                });

        challenge.setNumberOfParticipants(challenge.getNumberOfParticipants() + 1);

        ChallengeEnrollment challengeEnrollment = ChallengeEnrollment.of(member, challenge);
        challengeEnrollmentRepository.save(challengeEnrollment);

        return SuccessResponse.of(
                SuccessCode.ENROLL_CHALLENGE_SUCCESS,
                ChallengeEnrollmentResponse.of(
                        challenge, challengeEnrollment, member
                )
        );
    }

    private void validateChallengeEnrollmentTime(Challenge challenge) {
        ZonedDateTime now = ZonedDateTime.now();
        if (now.isAfter(challenge.getStartDate())) {
            throw new BadRequestException(ErrorCode.INVALID_CHALLENGE_REGISTRATION_TIME);
        }
    }
}
