package com.habitpay.habitpay.domain.challenge.api;

import com.habitpay.habitpay.domain.challenge.application.ChallengeCreationService;
import com.habitpay.habitpay.domain.challenge.application.ChallengeDetailsService;
import com.habitpay.habitpay.domain.challenge.application.ChallengeUpdateService;
import com.habitpay.habitpay.domain.challenge.dto.ChallengeCreationRequest;
import com.habitpay.habitpay.domain.challenge.dto.ChallengeDetailsResponse;
import com.habitpay.habitpay.domain.challenge.dto.ChallengePatchRequest;
import com.habitpay.habitpay.global.config.auth.CustomUserDetails;
import com.habitpay.habitpay.global.response.ApiResponse;
import com.habitpay.habitpay.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ChallengeApi {
    private final ChallengeCreationService challengeCreationService;
    private final ChallengeUpdateService challengeUpdateService;
    private final ChallengeDetailsService challengeDetailsService;

    @GetMapping("/challenges/{id}")
    public SuccessResponse<ChallengeDetailsResponse> getChallengeDetails(@PathVariable("id") Long id,
                                                                         @AuthenticationPrincipal CustomUserDetails user) {
        return challengeDetailsService.getChallengeDetails(id, user.getId());
    }

    @PostMapping("/challenges")
    public ResponseEntity<ApiResponse> createChallenge(@RequestBody ChallengeCreationRequest challengeCreationRequest,
                                                       @AuthenticationPrincipal CustomUserDetails user) {
        return challengeCreationService.save(challengeCreationRequest, user.getId());
    }

    @PatchMapping("/challenges/{id}")
    public ResponseEntity<ApiResponse> patchChallengeDetails(@PathVariable("id") Long id, @RequestBody ChallengePatchRequest challengePatchRequest,
                                                             @AuthenticationPrincipal CustomUserDetails user) {
        return challengeUpdateService.update(id, challengePatchRequest, user.getId());
    }
}
