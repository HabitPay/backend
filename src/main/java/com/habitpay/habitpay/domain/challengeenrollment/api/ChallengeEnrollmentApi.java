package com.habitpay.habitpay.domain.challengeenrollment.api;

import com.habitpay.habitpay.domain.challengeenrollment.application.ChallengeEnrollmentCancellationService;
import com.habitpay.habitpay.domain.challengeenrollment.application.ChallengeEnrollmentService;
import com.habitpay.habitpay.domain.challengeenrollment.dto.ChallengeEnrollmentResponse;
import com.habitpay.habitpay.global.config.auth.CustomUserDetails;
import com.habitpay.habitpay.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final ChallengeEnrollmentCancellationService challengeEnrollmentCancellationService;

    @PostMapping("/challenges/{id}/enroll")
    public SuccessResponse<ChallengeEnrollmentResponse> enrollChallenge(@PathVariable("id") Long id, @AuthenticationPrincipal CustomUserDetails user) {
        return challengeEnrollmentService.enroll(id, user.getMember());
    }

    @PostMapping("/challenges/{id}/cancel")
    public SuccessResponse<Void> cancelChallenge(@PathVariable("id") Long id, @AuthenticationPrincipal CustomUserDetails user) {
        return challengeEnrollmentCancellationService.cancel(id, user.getMember());
    }

}
