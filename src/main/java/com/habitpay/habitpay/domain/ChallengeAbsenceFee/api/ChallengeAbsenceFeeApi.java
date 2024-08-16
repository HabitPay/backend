package com.habitpay.habitpay.domain.ChallengeAbsenceFee.api;

import com.habitpay.habitpay.domain.ChallengeAbsenceFee.application.ChallengeAbsenceFeeSearchService;
import com.habitpay.habitpay.domain.ChallengeAbsenceFee.dto.FeeStatusResponse;
import com.habitpay.habitpay.domain.ChallengeAbsenceFee.dto.MemberFeeResponse;
import com.habitpay.habitpay.global.config.auth.CustomUserDetails;
import com.habitpay.habitpay.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ChallengeAbsenceFeeApi {

    private final ChallengeAbsenceFeeSearchService challengeAbsenceFeeSearchService;

    @GetMapping("/challenges/{id}/fee")
    public SuccessResponse<FeeStatusResponse> getFeeStatusByChallenge(@PathVariable("id") Long id,
                                                                      @AuthenticationPrincipal CustomUserDetails user) {

        return challengeAbsenceFeeSearchService.makeMemberFeeDataListOfChallenge(id, user.getMember());
    }
}