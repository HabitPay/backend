package com.habitpay.habitpay.domain.challengeenrollment.api;

import com.habitpay.habitpay.domain.challengeenrollment.application.ChallengeCancellationService;
import com.habitpay.habitpay.domain.challengeenrollment.application.ChallengeEnrollmentService;
import com.habitpay.habitpay.domain.member.application.MemberService;
import com.habitpay.habitpay.global.config.jwt.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChallengeEnrollmentApi {
    private final MemberService memberService;
    private final TokenService tokenService;
    private final ChallengeEnrollmentService challengeEnrollmentService;
    private final ChallengeCancellationService challengeCancellationService;

    @PostMapping("/challenges/{id}/enroll")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> enrollChallenge(@PathVariable("id") Long id, @AuthenticationPrincipal String email) {
        log.info("[POST /challenges/{}/enroll] email: {}", id, email);
        return challengeEnrollmentService.enroll(id, email);
    }

    @PostMapping("/challenges/{id}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> cancelChallenge(@PathVariable("id") Long id, @AuthenticationPrincipal String email) {
        log.info("[POST /challenges/{}/cancel]: email {}", id, email);
        return challengeCancellationService.cancel(id, email);
    }

}
