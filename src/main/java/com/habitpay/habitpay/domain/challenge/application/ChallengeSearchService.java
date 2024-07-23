package com.habitpay.habitpay.domain.challenge.application;

import com.habitpay.habitpay.domain.challenge.dao.ChallengeRepository;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challenge.dto.ChallengeEnrolledListItemResponse;
import com.habitpay.habitpay.domain.challengeenrollment.dao.ChallengeEnrollmentRepository;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengeparticipationrecord.dao.ChallengeParticipationRecordRepository;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.global.response.SuccessResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ChallengeSearchService {
    private final ChallengeRepository challengeRepository;
    private final ChallengeEnrollmentRepository challengeEnrollmentRepository;
    private final ChallengeParticipationRecordRepository challengeParticipationRecordRepository;

    public SuccessResponse<List<ChallengeEnrolledListItemResponse>> getEnrolledChallengeList(Member member) {
        List<ChallengeEnrollment> challengeEnrollmentList = challengeEnrollmentRepository.findAllByMember(member);
        List<ChallengeEnrolledListItemResponse> response = challengeEnrollmentList.stream()
                .map(this::mapToResponse)
                .toList();

        return SuccessResponse.of("", response);
    }

    @Transactional(readOnly = true)
    public Challenge getChallengeById(Long id) {
        // TODO: 공통 예외처리 적용하기
        return challengeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하는 챌린지가 아닙니다."));
    }

    private ChallengeEnrolledListItemResponse mapToResponse(ChallengeEnrollment challengeEnrollment) {
        Challenge challenge = challengeEnrollment.getChallenge();
        boolean isParticipatedToday = challengeParticipationRecordRepository
                .findByChallengeEnrollment(challengeEnrollment)
                .isPresent();
        return ChallengeEnrolledListItemResponse.of(challenge, challengeEnrollment, isParticipatedToday);
    }
}
