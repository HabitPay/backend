package com.habitpay.habitpay.domain.challenge.application;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challenge.dto.ChallengeDetailsResponse;
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

        // TODO: Challenge 엔티티에 totalAbsenceFee 컬럼 추가 후 Response 생성 시 전달하는 매개변수도 함께 변경 부탁드립니다. (from. joonhan)
        int totalAbsenceFee = sumAllFeesOfChallenge(challenge);

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
}
