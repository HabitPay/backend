package com.habitpay.habitpay.domain.challengeenrollment.application;

import com.habitpay.habitpay.domain.challenge.application.ChallengeSearchService;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.dao.ChallengeEnrollmentRepository;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.member.application.MemberSearchService;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeEnrollmentCancellationService {
    private final ChallengeEnrollmentRepository challengeEnrollmentRepository;
    private final ChallengeSearchService challengeSearchService;
    private final MemberSearchService memberSearchService;

    @Transactional
    public ResponseEntity<ApiResponse> cancel(Long challengeId, Long userId) {
        ApiResponse response;
        Member member = memberSearchService.getMemberById(userId);
        Optional<ChallengeEnrollment> optionalChallengeEnrollment = challengeEnrollmentRepository.findByMember(member);
        if (optionalChallengeEnrollment.isEmpty()) {
            log.error("참여하지 않은 챌린지");
            response = ApiResponse.create("참여하지 않은 챌린지입니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Challenge challenge = challengeSearchService.getChallengeById(challengeId);
        ZonedDateTime now = ZonedDateTime.now();
        if (now.isAfter(challenge.getStartDate())) {
            log.error("챌린지 취소 시간 초과");
            response = ApiResponse.create("챌린지 취소 가능한 시간이 지났습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        ChallengeEnrollment challengeEnrollment = optionalChallengeEnrollment.get();
        challengeEnrollmentRepository.delete(challengeEnrollment);
        log.info("챌린지 참여 취소");
        response = ApiResponse.create("정상적으로 챌린지 참여를 취소했습니다.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
