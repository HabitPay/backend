package com.habitpay.habitpay.domain.member.api;

import com.habitpay.habitpay.domain.member.application.*;
import com.habitpay.habitpay.domain.member.dto.*;
import com.habitpay.habitpay.global.config.auth.CustomUserDetails;
import com.habitpay.habitpay.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class MemberApi {
    private final MemberSearchService memberSearchService;
    private final MemberUpdateService memberUpdateService;
    private final MemberDeleteService memberDeleteService;
    private final MemberDetailsService memberDetailsService;

    @GetMapping("/member")
    public SuccessResponse<MemberProfileResponse> getMember(@AuthenticationPrincipal CustomUserDetails user) {
        return memberSearchService.getMemberProfile(user.getMember());
    }

    @GetMapping("/members/{id}")
    public SuccessResponse<MemberDetailsResponse> getMemberDetails(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal CustomUserDetails user) {
        return memberDetailsService.getMemberDetails(id, user.getMember());
    }

    @PatchMapping("/member/nickname")
    public SuccessResponse<NicknameDto> patchNickname(@RequestBody NicknameDto nicknameDto,
                                                      @AuthenticationPrincipal CustomUserDetails user) {
        return memberUpdateService.updateNickname(nicknameDto, user.getMember());
    }

    @PatchMapping("/member/image")
    public SuccessResponse<ImageUpdateResponse> patchImage(@RequestBody ImageUpdateRequest imageUpdateRequest,
                                                           @AuthenticationPrincipal CustomUserDetails user) {
        return memberUpdateService.updateImage(imageUpdateRequest, user.getMember());
    }

    @DeleteMapping("/member")
    @ResponseStatus(HttpStatus.OK)
    public SuccessResponse<Long> deleteMember(@AuthenticationPrincipal CustomUserDetails user) {
        return memberDeleteService.delete(user.getMember());
    }
}
