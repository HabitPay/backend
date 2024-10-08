package com.habitpay.habitpay.domain.challenge.application;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challenge.dto.ChallengeDatesResponse;
import com.habitpay.habitpay.domain.challenge.dto.ChallengeDetailsResponse;
import com.habitpay.habitpay.domain.challenge.dto.ChallengeFeePerAbsenceResponse;
import com.habitpay.habitpay.domain.challenge.dto.ChallengeParticipatingDaysResponse;
import com.habitpay.habitpay.domain.challengeenrollment.dao.ChallengeEnrollmentRepository;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.global.config.aws.S3FileService;
import com.habitpay.habitpay.global.response.SuccessCode;
import com.habitpay.habitpay.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeDetailsService {
    private final S3FileService s3FileService;
    private final ChallengeSearchService challengeSearchService;
    private final ChallengeEnrollmentRepository challengeEnrollmentRepository;

    public SuccessResponse<ChallengeDetailsResponse> getChallengeDetails(Long challengeId, Member member) {
        Challenge challenge = challengeSearchService.getChallengeById(challengeId);
        Boolean isMemberEnrolledInChallenge = challengeEnrollmentRepository.findByMemberAndChallenge(member, challenge)
                .isPresent();
        List<ChallengeEnrollment> challengeEnrollmentList = challengeEnrollmentRepository.findTop3ByChallenge(challenge);
        List<String> enrolledMembersProfileImageList = challengeEnrollmentList.stream()
                .map((challengeEnrollment) -> {
                    return Optional.ofNullable(challengeEnrollment.getMember().getImageFileName())
                            .map((imageFileName) -> s3FileService.getGetPreSignedUrl("profiles", imageFileName))
                            .orElse("");
                })
                .toList();

        List<ChallengeEnrollment> enrollmentList = challengeEnrollmentRepository.findAllByChallenge(challenge);
        int totalAbsenceFee = enrollmentList
                .stream()
                .mapToInt(enrollment -> enrollment.getParticipationStat().getTotalFee())
                .sum();

        return SuccessResponse.of(
                SuccessCode.NO_MESSAGE,
                ChallengeDetailsResponse.of(
                        member,
                        challenge,
                        totalAbsenceFee,
                        enrolledMembersProfileImageList,
                        isMemberEnrolledInChallenge)
        );
    }

    public SuccessResponse<ChallengeFeePerAbsenceResponse> getChallengeFeePerAbsence(Long challengeId) {
        Challenge challenge = challengeSearchService.getChallengeById(challengeId);

        return SuccessResponse.of(
                SuccessCode.NO_MESSAGE,
                ChallengeFeePerAbsenceResponse.from(challenge)
        );
    }

    public SuccessResponse<ChallengeDatesResponse> getChallengeDates(Long challengeId) {
        Challenge challenge = challengeSearchService.getChallengeById(challengeId);

        return SuccessResponse.of(
                SuccessCode.NO_MESSAGE,
                ChallengeDatesResponse.from(challenge)
        );
    }

    public SuccessResponse<ChallengeParticipatingDaysResponse> getChallengeParticipatingDays(Long challengeId) {
        Challenge challenge = challengeSearchService.getChallengeById(challengeId);

        return SuccessResponse.of(
                SuccessCode.NO_MESSAGE,
                ChallengeParticipatingDaysResponse.from(challenge)
        );
    }
}
