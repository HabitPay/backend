package com.habitpay.habitpay.domain.challenge.api;

import com.habitpay.habitpay.domain.challenge.application.*;
import com.habitpay.habitpay.domain.challenge.dto.*;
import com.habitpay.habitpay.global.config.auth.CustomUserDetails;
import com.habitpay.habitpay.global.response.PageResponse;
import com.habitpay.habitpay.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ChallengeApi {

    private final ChallengeCreationService challengeCreationService;
    private final ChallengePatchService challengePatchService;
    private final ChallengeDetailsService challengeDetailsService;
    private final ChallengeSearchService challengeSearchService;
    private final ChallengeDeleteService challengeDeleteService;

    @GetMapping("/challenges")
    public SuccessResponse<PageResponse<ChallengePageResponse>> getChallengePage(
            @PageableDefault(size = 20, sort = "startDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return challengeSearchService.getChallengePage(pageable);
    }

    @GetMapping("/challenges/me")
    public SuccessResponse<List<ChallengeEnrolledListItemResponse>> getEnrolledChallengeList(
            @AuthenticationPrincipal CustomUserDetails user) {
        return challengeSearchService.getEnrolledChallengeList(user.getMember());
    }

    @GetMapping("/challenges/{id}")
    public SuccessResponse<ChallengeDetailsResponse> getChallengeDetails(@PathVariable("id") Long id,
                                                                         @AuthenticationPrincipal CustomUserDetails user) {
        return challengeDetailsService.getChallengeDetails(id, user.getMember());
    }

    @GetMapping("/challenges/{id}/fees/absence")
    public SuccessResponse<ChallengeFeePerAbsenceResponse> getChallengeFeePerAbsence(@PathVariable("id") Long id) {
        return challengeDetailsService.getChallengeFeePerAbsence(id);
    }

    @GetMapping("/challenges/{id}/fees/absence/total")
    public SuccessResponse<ChallengeTotalAbsenceFeeResponse> getChallengeTotalAbsenceFee(@PathVariable("id") Long id) {
        return challengeDetailsService.getChallengeTotalAbsenceFee(id);
    }

    @GetMapping("/challenges/{id}/dates")
    public SuccessResponse<ChallengeDatesResponse> getChallengeDates(@PathVariable("id") Long id) {
        return challengeDetailsService.getChallengeDates(id);
    }

    @GetMapping("/challenges/{id}/participating-days")
    public SuccessResponse<ChallengeParticipatingDaysResponse> getChallengeParticipatingDays(@PathVariable("id") Long id) {
        return challengeDetailsService.getChallengeParticipatingDays(id);
    }

    @PostMapping("/challenges")
    public SuccessResponse<ChallengeCreationResponse> createChallenge(@RequestBody ChallengeCreationRequest challengeCreationRequest,
                                                                      @AuthenticationPrincipal CustomUserDetails user) {
        return challengeCreationService.createChallenge(challengeCreationRequest, user.getMember());
    }

    @PatchMapping("/challenges/{id}")
    public SuccessResponse<ChallengePatchResponse> patchChallengeDetails(@PathVariable("id") Long id, @RequestBody ChallengePatchRequest challengePatchRequest,
                                                                         @AuthenticationPrincipal CustomUserDetails user) {
        return challengePatchService.patch(id, challengePatchRequest, user.getMember());
    }

    @DeleteMapping("/challenges/{id}")
    public SuccessResponse<Void> deleteChallenge(@PathVariable("id") Long id,
                                                 @AuthenticationPrincipal CustomUserDetails user) {
        return challengeDeleteService.delete(id, user.getId());
    }

}
