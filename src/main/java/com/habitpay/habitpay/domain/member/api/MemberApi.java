package com.habitpay.habitpay.domain.member.api;

import com.habitpay.habitpay.domain.member.application.MemberActivationService;
import com.habitpay.habitpay.domain.member.application.MemberUpdateService;
import com.habitpay.habitpay.domain.member.application.MemberSearchService;
import com.habitpay.habitpay.domain.member.application.MemberService;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.member.dto.*;
import com.habitpay.habitpay.global.config.auth.CustomUserDetails;
import com.habitpay.habitpay.global.config.aws.S3FileService;
import com.habitpay.habitpay.global.config.jwt.TokenService;
import com.habitpay.habitpay.global.error.ErrorResponse;
import com.habitpay.habitpay.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class MemberApi {
    private final MemberService memberService;
    private final MemberSearchService memberSearchService;
    private final MemberActivationService memberActivationService;
    private final MemberUpdateService memberUpdateService;
    private final TokenService tokenService;
    private final S3FileService s3FileService;

    @GetMapping("/member")
    public SuccessResponse<MemberProfileResponse> getMember(@AuthenticationPrincipal CustomUserDetails user) {
        return memberSearchService.getMemberProfile(user.getId());
    }

    // TODO: /member/activate 로 변경하기
    @PostMapping("/member")
    public SuccessResponse<MemberActivationResponse> activateMember(
            @RequestBody MemberActivationRequest memberActivationRequest,
            @AuthenticationPrincipal CustomUserDetails user) {
        log.info("[POST /member] email: {}, nickname: {}", user.getEmail(), memberActivationRequest.getNickname());
        return memberActivationService.activate(memberActivationRequest, user.getId());
    }

    @PatchMapping("/member/nickname")
    public SuccessResponse<NicknameDto> patchNickname(@RequestBody NicknameDto nicknameDto,
                                                      @AuthenticationPrincipal CustomUserDetails user) {
        return memberUpdateService.updateNickname(nicknameDto, user.getId());
    }

    @PatchMapping("/member/image")
    public SuccessResponse<ImageUpdateResponse> patchImage(@RequestBody ImageUpdateRequest imageUpdateRequest,
                                                           @AuthenticationPrincipal CustomUserDetails user) {
        return memberUpdateService.updateImage(imageUpdateRequest, user.getId());
    }

    @DeleteMapping("/member")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteMember(@RequestHeader("Authorization") String authorizationHeader) {
        Optional<String> optionalToken = tokenService.getTokenFromHeader(authorizationHeader);
        if (optionalToken.isEmpty()) {
            String message = ErrorResponse.UNAUTHORIZED.getMessage();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
        }

        String token = optionalToken.get();
        String email = tokenService.getEmail(token);
        Member member = memberService.findByEmail(email);

        String imageFileName = member.getImageFileName();
        log.info("[DELETE /member] imageFileName: {}", imageFileName);
        s3FileService.deleteImage("profiles", imageFileName);
        memberService.delete(member);
        return ResponseEntity.status(HttpStatus.OK).body("정상적으로 탈퇴되었습니다.");
    }
}
