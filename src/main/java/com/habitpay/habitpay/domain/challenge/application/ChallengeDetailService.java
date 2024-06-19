package com.habitpay.habitpay.domain.challenge.application;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challenge.dto.ChallengeResponse;
import com.habitpay.habitpay.domain.challengeenrollment.application.ChallengeEnrollmentSearchService;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.member.application.MemberService;
import com.habitpay.habitpay.domain.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChallengeDetailService {
    private final MemberService memberService;
    private final ChallengeSearchService challengeSearchService;
    private final ChallengeEnrollmentSearchService challengeEnrollmentSearchService;

    public ResponseEntity<ChallengeResponse> findById(Long id, String email) {
        Member member = memberService.findByEmail(email);
        Challenge challenge = challengeSearchService.findById(id);
        Optional<ChallengeEnrollment> optionalChallengeEnrollment = challengeEnrollmentSearchService.findByMember(member);
        ChallengeResponse challengeResponse = new ChallengeResponse(member, challenge, optionalChallengeEnrollment);

        return ResponseEntity.status(HttpStatus.OK).body(challengeResponse);
    }
}
