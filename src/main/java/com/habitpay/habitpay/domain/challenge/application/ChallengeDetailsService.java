package com.habitpay.habitpay.domain.challenge.application;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challenge.dto.ChallengeDetailsResponse;
import com.habitpay.habitpay.domain.challengeenrollment.dao.ChallengeEnrollmentRepository;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengeparticipationrecord.application.ChallengeParticipationRecordSearchService;
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
    private final ChallengeParticipationRecordSearchService challengeParticipationRecordSearchService;
    private final ChallengeEnrollmentRepository challengeEnrollmentRepository;

    public SuccessResponse<ChallengeDetailsResponse> getChallengeDetails(Long challengeId, Member member) {
        Challenge challenge = challengeSearchService.getChallengeById(challengeId);
        ChallengeEnrollment enrollment = challengeEnrollmentRepository.findByMemberAndChallenge(member, challenge).orElse(null);

        boolean isMemberEnrolledInChallenge = enrollment != null;
        boolean isParticipatedToday = isMemberEnrolledInChallenge && hasParticipatedToday(enrollment);
        boolean isGivenUp = isMemberEnrolledInChallenge && enrollment.isGivenUp();

        List<String> enrolledMembersProfileImageList = getEnrolledMembersProfileImages(challenge);

        return SuccessResponse.of(
                SuccessCode.NO_MESSAGE,
                ChallengeDetailsResponse.of(
                        member,
                        challenge,
                        enrolledMembersProfileImageList,
                        isMemberEnrolledInChallenge,
                        isParticipatedToday,
                        isGivenUp)
        );
    }

    private boolean hasParticipatedToday(ChallengeEnrollment enrollment) {
        return challengeParticipationRecordSearchService.hasParticipationPostForToday(enrollment);
    }

    private List<String> getEnrolledMembersProfileImages(Challenge challenge) {
        List<ChallengeEnrollment> challengeEnrollmentList = challengeEnrollmentRepository.findTop3ByChallenge(challenge);
        return challengeEnrollmentList.stream()
                .map(this::getProfileImageUrl)
                .toList();
    }

    private String getProfileImageUrl(ChallengeEnrollment enrollment) {
        return Optional.ofNullable(enrollment.getMember().getImageFileName())
                .map(imageFileName -> s3FileService.getGetPreSignedUrl("profiles", imageFileName))
                .orElse("");
    }
}
