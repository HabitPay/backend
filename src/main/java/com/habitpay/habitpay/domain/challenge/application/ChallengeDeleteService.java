package com.habitpay.habitpay.domain.challenge.application;

import com.habitpay.habitpay.domain.challenge.dao.ChallengeRepository;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.application.ChallengeEnrollmentSearchService;
import com.habitpay.habitpay.domain.challengeenrollment.dao.ChallengeEnrollmentRepository;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.member.application.MemberSearchService;
import com.habitpay.habitpay.domain.member.application.MemberUtilsService;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.global.config.timezone.TimeZoneConverter;
import com.habitpay.habitpay.global.error.exception.BadRequestException;
import com.habitpay.habitpay.global.error.exception.ErrorCode;
import com.habitpay.habitpay.global.error.exception.ForbiddenException;
import com.habitpay.habitpay.global.response.SuccessCode;
import com.habitpay.habitpay.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeDeleteService {
    private final ChallengeSearchService challengeSearchService;
    private final ChallengeRepository challengeRepository;
    private final ChallengeEnrollmentRepository challengeEnrollmentRepository;
    private final MemberUtilsService memberUtilsService;
    private final ChallengeEnrollmentSearchService challengeEnrollmentSearchService;

    public SuccessResponse<Void> delete(Long challengeId, Member member) {
        Challenge challenge = challengeSearchService.getChallengeById(challengeId);

        if (!memberUtilsService.isChallengeHost(challenge, member)) {
            throw new ForbiddenException(ErrorCode.NOT_ALLOWED_TO_DELETE_CHALLENGE);
        }

        ZonedDateTime now = TimeZoneConverter.convertEtcToLocalTimeZone(ZonedDateTime.now());

        if (!now.isBefore(challenge.getStartDate())) {
            throw new BadRequestException(ErrorCode.TOO_EARLY_GIVEN_UP_CHALLENGE);
        }

        List<ChallengeEnrollment> enrollmentList = challengeEnrollmentSearchService.findAllByChallenge(challenge);
        challengeEnrollmentRepository.deleteAll(enrollmentList);

        challengeRepository.delete(challenge);

        return SuccessResponse.of(SuccessCode.DELETE_CHALLENGE_SUCCESS);
    }
}
