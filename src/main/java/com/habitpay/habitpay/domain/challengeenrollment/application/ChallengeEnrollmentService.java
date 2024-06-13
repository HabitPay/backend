package com.habitpay.habitpay.domain.challengeenrollment.application;

import com.habitpay.habitpay.domain.challenge.application.ChallengeSearchService;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.dao.ChallengeEnrollmentRepository;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.member.application.MemberService;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.global.common.response.ApiResponse;
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
public class ChallengeEnrollmentService {
    private final MemberService memberService;
    private final ChallengeEnrollmentRepository challengeEnrollmentRepository;
    private final ChallengeSearchService challengeSearchService;

    @Transactional
    public ResponseEntity<ApiResponse> enroll(Long id, String email) {
        ApiResponse response;
        Challenge challenge = challengeSearchService.findById(id);
        ZonedDateTime now = ZonedDateTime.now();
        if (now.isAfter(challenge.getEndDate())) {
            log.error("챌린지 등록 기간 초과");
            response = ApiResponse.create("챌린지 참여 가능한 시간이 지났습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Member member = memberService.findByEmail(email);
        Optional<ChallengeEnrollment> optionalChallengeEnrollment = challengeEnrollmentRepository.findByMember(member);
        if (optionalChallengeEnrollment.isPresent()) {
            log.error("이미 참여한 챌린지");
            response = ApiResponse.create("이미 참여한 챌린지입니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        ChallengeEnrollment challengeEnrollment = ChallengeEnrollment.builder()
                .member(member)
                .challenge(challenge)
                .enrolledDate(now)
                .build();

        challengeEnrollmentRepository.save(challengeEnrollment);
        log.info("챌린지 참여 완료");
        response = ApiResponse.create("챌린지에 정상적으로 참여했습니다.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
