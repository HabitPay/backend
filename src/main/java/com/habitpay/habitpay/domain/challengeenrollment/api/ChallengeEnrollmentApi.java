package com.habitpay.habitpay.domain.challengeenrollment.api;

import com.habitpay.habitpay.domain.challengeenrollment.application.ChallengeCancellationService;
import com.habitpay.habitpay.domain.challengeenrollment.application.ChallengeEnrollmentService;
import com.habitpay.habitpay.global.config.auth.CustomUserDetails;
import com.habitpay.habitpay.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChallengeEnrollmentApi {
    private final ChallengeEnrollmentService challengeEnrollmentService;
    private final ChallengeCancellationService challengeCancellationService;

    @PostMapping("/challenges/{id}/enroll")
    public ResponseEntity<ApiResponse> enrollChallenge(@PathVariable("id") Long id, @AuthenticationPrincipal CustomUserDetails user) {
        return challengeEnrollmentService.enroll(id, user.getId());
    }

    @PostMapping("/challenges/{id}/cancel")
    public ResponseEntity<ApiResponse> cancelChallenge(@PathVariable("id") Long id, @AuthenticationPrincipal CustomUserDetails user) {
        return challengeCancellationService.cancel(id, user.getId());
    }

}
