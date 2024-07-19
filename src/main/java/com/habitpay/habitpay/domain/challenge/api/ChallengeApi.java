package com.habitpay.habitpay.domain.challenge.api;

import com.habitpay.habitpay.domain.challenge.application.ChallengeCreationService;
import com.habitpay.habitpay.domain.challenge.application.ChallengeDetailsService;
import com.habitpay.habitpay.domain.challenge.application.ChallengePatchService;
import com.habitpay.habitpay.domain.challenge.dto.*;
import com.habitpay.habitpay.global.config.auth.CustomUserDetails;
import com.habitpay.habitpay.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ChallengeApi {
    private final ChallengeCreationService challengeCreationService;
    private final ChallengePatchService challengePatchService;
    private final ChallengeDetailsService challengeDetailsService;

    @GetMapping("/challenges/{id}")
    public SuccessResponse<ChallengeDetailsResponse> getChallengeDetails(@PathVariable("id") Long id,
                                                                         @AuthenticationPrincipal CustomUserDetails user) {
        return challengeDetailsService.getChallengeDetails(id, user.getId());
    }

    @PostMapping("/challenges")
    public SuccessResponse<ChallengeCreationResponse> createChallenge(@RequestBody ChallengeCreationRequest challengeCreationRequest,
                                                                      @AuthenticationPrincipal CustomUserDetails user) {
        return challengeCreationService.createChallenge(challengeCreationRequest, user.getId());
    }

    @PatchMapping("/challenges/{id}")
    public SuccessResponse<ChallengePatchResponse> patchChallengeDetails(@PathVariable("id") Long id, @RequestBody ChallengePatchRequest challengePatchRequest,
                                                                         @AuthenticationPrincipal CustomUserDetails user) {
        return challengePatchService.patch(id, challengePatchRequest, user.getId());
    }
}
