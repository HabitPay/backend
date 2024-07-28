package com.habitpay.habitpay.domain.challengeenrollment.application;

import com.habitpay.habitpay.domain.challenge.application.ChallengeSearchService;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.dao.ChallengeEnrollmentRepository;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengeenrollment.exception.NotEnrolledChallengeException;
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
public class ChallengeEnrollmentCancellationService {
    private final ChallengeEnrollmentRepository challengeEnrollmentRepository;
    private final ChallengeSearchService challengeSearchService;

    @Transactional
    public SuccessResponse<Void> cancel(Long challengeId, Member member) {

        ChallengeEnrollment challengeEnrollment = challengeEnrollmentRepository.findByMember(member)
                .orElseThrow(() -> new NotEnrolledChallengeException(challengeId, member.getId()));

        Challenge challenge = challengeSearchService.getChallengeById(challengeId);

        validateChallengeHost(member, challenge);
        validateCancellationTime(challenge);

        challenge.setNumberOfParticipants(challenge.getNumberOfParticipants() - 1);
        challengeEnrollmentRepository.delete(challengeEnrollment);
        return SuccessResponse.<Void>of(SuccessCode.CANCEL_CHALLENGE_ENROLLMENT_SUCCESS);
    }

    private void validateCancellationTime(Challenge challenge) {
        ZonedDateTime now = ZonedDateTime.now();
        if (now.isAfter(challenge.getStartDate())) {
            throw new BadRequestException(ErrorCode.INVALID_CHALLENGE_CANCELLATION_TIME);
        }
    }

    private void validateChallengeHost(Member member, Challenge challenge) {
        Member host = challenge.getHost();
        if (host.getId().equals(member.getId())) {
            throw new BadRequestException(ErrorCode.NOT_ALLOWED_TO_CANCEL_ENROLLMENT_OF_HOST);
        }
    }

}
