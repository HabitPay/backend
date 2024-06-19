package com.habitpay.habitpay.domain.challenge.api;

import com.habitpay.habitpay.domain.challenge.application.ChallengeCreationService;
import com.habitpay.habitpay.domain.challenge.application.ChallengeDetailService;
import com.habitpay.habitpay.domain.challenge.application.ChallengeUpdateService;
import com.habitpay.habitpay.domain.challenge.dto.ChallengeCreationRequest;
import com.habitpay.habitpay.domain.challenge.dto.ChallengePatchRequest;
import com.habitpay.habitpay.domain.challenge.dto.ChallengeResponse;
import com.habitpay.habitpay.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    private final ChallengeDetailService challengeDetailService;

    @GetMapping("/challenges/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ChallengeResponse> getChallengeDetail(@PathVariable("id") Long id,
                                                                @AuthenticationPrincipal String email) {
        return challengeDetailService.findById(id, email);
    }

    @PostMapping("/challenges")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse> createChallenge(@RequestBody ChallengeCreationRequest challengeCreationRequest,
                                                       @AuthenticationPrincipal String email) {
        return challengeCreationService.save(challengeCreationRequest, email);
    }

    @PatchMapping("/challenges/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse> patchChallengeDetails(@PathVariable("id") Long id, @RequestBody ChallengePatchRequest challengePatchRequest,
                                                             @AuthenticationPrincipal String email) {
        return challengeUpdateService.update(id, challengePatchRequest, email);
    }
}
