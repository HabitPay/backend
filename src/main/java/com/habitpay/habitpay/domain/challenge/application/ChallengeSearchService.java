package com.habitpay.habitpay.domain.challenge.application;

import com.habitpay.habitpay.domain.challenge.dao.ChallengeRepository;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challenge.dto.ChallengeEnrolledListItemResponse;
import com.habitpay.habitpay.domain.challenge.exception.ChallengeNotFoundException;
import com.habitpay.habitpay.domain.challengeenrollment.dao.ChallengeEnrollmentRepository;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengeparticipationrecord.dao.ChallengeParticipationRecordRepository;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.global.config.aws.S3FileService;
import com.habitpay.habitpay.global.response.SuccessCode;
import com.habitpay.habitpay.global.response.SuccessResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class ChallengeSearchService {

    private final S3FileService s3FileService;
    private final ChallengeRepository challengeRepository;
    private final ChallengeEnrollmentRepository challengeEnrollmentRepository;
    private final ChallengeParticipationRecordRepository challengeParticipationRecordRepository;

    public SuccessResponse<List<ChallengeEnrolledListItemResponse>> getEnrolledChallengeList(Member member) {
        List<ChallengeEnrollment> challengeEnrollmentList = challengeEnrollmentRepository.findAllByMember(member);
        List<ChallengeEnrolledListItemResponse> response = challengeEnrollmentList.stream()
                .map(this::mapToResponse)
                .toList();

        return SuccessResponse.of(SuccessCode.NO_MESSAGE, response);
    }

    @Transactional(readOnly = true)
    public Challenge getChallengeById(Long id) {
        return challengeRepository.findById(id)
                .orElseThrow(() -> new ChallengeNotFoundException(id));
    }

    private ChallengeEnrolledListItemResponse mapToResponse(ChallengeEnrollment challengeEnrollment) {
        Challenge challenge = challengeEnrollment.getChallenge();
        boolean isParticipatedToday = challengeParticipationRecordRepository
                .findByChallengeEnrollment(challengeEnrollment)
                .isPresent();
        String hostProfileImageUrl = Optional.ofNullable(challenge.getHost().getImageFileName())
                .map((imageFileName) -> s3FileService.getGetPreSignedUrl("profiles", imageFileName))
                .orElse("");
        return ChallengeEnrolledListItemResponse.of(challenge, challengeEnrollment, hostProfileImageUrl, isParticipatedToday);
    }
}
