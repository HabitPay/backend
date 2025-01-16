package com.habitpay.habitpay.domain.challengeenrollment.application;

import com.habitpay.habitpay.domain.challenge.application.ChallengeSearchService;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.dao.ChallengeEnrollmentRepository;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengeenrollment.exception.AlreadyGivenUpChallengeException;
import com.habitpay.habitpay.domain.challengeparticipationrecord.dao.ChallengeParticipationRecordRepository;
import com.habitpay.habitpay.domain.member.application.MemberUtilsService;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.global.config.timezone.TimeZoneConverter;
import com.habitpay.habitpay.global.error.exception.BadRequestException;
import com.habitpay.habitpay.global.error.exception.ErrorCode;
import com.habitpay.habitpay.global.response.SuccessCode;
import com.habitpay.habitpay.global.response.SuccessResponse;
import jakarta.transaction.Transactional;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeEnrollmentCancellationService {

    private final ChallengeEnrollmentRepository challengeEnrollmentRepository;
    private final ChallengeParticipationRecordRepository challengeParticipationRecordRepository;
    private final ChallengeSearchService challengeSearchService;
    private final ChallengeEnrollmentSearchService challengeEnrollmentSearchService;
    private final MemberUtilsService memberUtilsService;

    @Transactional
    public SuccessResponse<Void> cancel(Long challengeId, Member member) {

        Challenge challenge = challengeSearchService.getChallengeById(challengeId);
        ChallengeEnrollment challengeEnrollment = challengeEnrollmentSearchService.getByMemberAndChallenge(
            member, challenge);

        validateChallengeHost(member, challenge);
        validateCancellationTime(challenge);

        challenge.setNumberOfParticipants(challenge.getNumberOfParticipants() - 1);
        challengeEnrollmentRepository.delete(challengeEnrollment);
        return SuccessResponse.of(SuccessCode.CANCEL_CHALLENGE_ENROLLMENT_SUCCESS);
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

    @Transactional
    public SuccessResponse<Void> giveUp(Long challengeId, Member member) {

        Challenge challenge = challengeSearchService.getChallengeById(challengeId);
        ChallengeEnrollment challengeEnrollment = challengeEnrollmentSearchService.getByMemberAndChallenge(
            member, challenge);
        ZonedDateTime now = TimeZoneConverter.convertEtcToLocalTimeZone(ZonedDateTime.now());

        if (memberUtilsService.isChallengeHost(challenge, member)) {
            throw new BadRequestException(ErrorCode.NOT_ALLOWED_TO_CANCEL_ENROLLMENT_OF_HOST);
        }

        if (now.isBefore(challenge.getStartDate())) {
            throw new BadRequestException(ErrorCode.TOO_EARLY_GIVEN_UP_CHALLENGE);
        }

        if (challengeEnrollment.isGivenUp()) {
            throw new AlreadyGivenUpChallengeException(member.getId(), challengeId);
        }

        challengeEnrollment.setGivenUp(true);

        challenge.setNumberOfParticipants(challenge.getNumberOfParticipants() - 1);

        challengeParticipationRecordRepository.deleteAllByChallengeEnrollmentAndTargetDateAfterOrTargetDate(
            challengeEnrollment, now.with(LocalTime.MIDNIGHT));

        return SuccessResponse.of(SuccessCode.GIVING_UP_CHALLENGE);
    }

}
