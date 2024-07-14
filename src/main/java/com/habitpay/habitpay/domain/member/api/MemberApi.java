package com.habitpay.habitpay.domain.member.api;

import com.habitpay.habitpay.domain.member.application.MemberActivationService;
import com.habitpay.habitpay.domain.member.application.MemberProfileService;
import com.habitpay.habitpay.domain.member.application.MemberSearchService;
import com.habitpay.habitpay.domain.member.application.MemberService;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.member.dto.MemberActivationRequest;
import com.habitpay.habitpay.domain.member.dto.MemberActivationResponse;
import com.habitpay.habitpay.domain.member.dto.MemberResponse;
import com.habitpay.habitpay.domain.member.dto.MemberUpdateRequest;
import com.habitpay.habitpay.domain.model.Response;
import com.habitpay.habitpay.domain.refreshtoken.application.RefreshTokenCreationService;
import com.habitpay.habitpay.global.config.auth.CustomUserDetails;
import com.habitpay.habitpay.global.config.aws.S3FileService;
import com.habitpay.habitpay.global.config.jwt.TokenService;
import com.habitpay.habitpay.global.error.ErrorResponse;
import com.habitpay.habitpay.global.response.SuccessResponse;
import com.habitpay.habitpay.global.util.ImageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class MemberApi {
    private final MemberService memberService;
    private final MemberSearchService memberSearchService;
    private final MemberActivationService memberActivationService;
    private final MemberProfileService memberProfileService;
    private final TokenService tokenService;
    private final RefreshTokenCreationService refreshTokenCreationService;
    private final S3FileService s3FileService;

    @GetMapping("/member")
    public ResponseEntity<MemberResponse> getMember(@AuthenticationPrincipal CustomUserDetails user) {
        MemberResponse memberResponse = memberSearchService.getMemberProfile(user.getId());
        return ResponseEntity.ok(memberResponse);
    }

    // TODO: /member/activate 로 변경하기
    @PostMapping("/member")
    public SuccessResponse<MemberActivationResponse> activateMember(
            @RequestBody MemberActivationRequest memberActivationRequest,
            @AuthenticationPrincipal CustomUserDetails user) {
        log.info("[POST /member] email: {}, nickname: {}", user.getEmail(), memberActivationRequest.getNickname());
        return memberActivationService.activate(memberActivationRequest, user.getId());
    }

    @PatchMapping("/member")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> patchMember(@RequestBody MemberUpdateRequest memberUpdateRequest,
                                              @RequestHeader("Authorization") String authorizationHeader) {
        // TODO: Interceptor 나 Filter 에서 먼저 처리해주기 때문에 나중에 삭제하기
        Optional<String> optionalToken = tokenService.getTokenFromHeader(authorizationHeader);
        if (optionalToken.isEmpty()) {
            String message = ErrorResponse.UNAUTHORIZED.getMessage();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
        }
        String nickname = memberUpdateRequest.getNickname();
        String imageExtension = memberUpdateRequest.getImageExtension();
        log.info("[PATCH /member] nickname: {}, imageExtension: {}", nickname, imageExtension);

        String token = optionalToken.get();
        String email = tokenService.getEmail(token);
        Member member = memberService.findByEmail(email);

        Long contentLength = memberUpdateRequest.getContentLength();

        // 1. 이미지 크기 제한이 넘을 경우
        if (ImageUtil.isValidFileSize(contentLength) == false) {
            String message = ErrorResponse.IMAGE_CONTENT_TOO_LARGE.getMessage();
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(message);
        }

        // 2. 이미지 확장자가 허용되지 않은 경우
        if (ImageUtil.isValidImageExtension(imageExtension) == false) {
            String message = ErrorResponse.UNSUPPORTED_IMAGE_EXTENSION.getMessage();
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(message);
        }

        // 3. 닉네임 규칙이 맞지 않은 경우
        if (memberProfileService.isNicknameValidFormat(nickname) == false) {
            String message = ErrorResponse.INVALID_NICKNAME_RULE.getMessage();
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(message);
        }

        // 4. 닉네임만 변경
        if (imageExtension.isEmpty()) {
            member.updateProfile(nickname, member.getImageFileName());
            memberService.save(member);
            String message = Response.PROFILE_UPDATE_SUCCESS.getMessage();
            return ResponseEntity.status(HttpStatus.OK).body(message);
        }

        // 5. 프로필 이미지가 이미 존재하고, 새롭게 업로드 하는 경우
        s3FileService.deleteImage("profiles", member.getImageFileName());

        String randomFileName = UUID.randomUUID().toString();
        String savedFileName = String.format("%s.%s", randomFileName, imageExtension);
        log.info("[PATCH /member] savedFileName: {}", savedFileName);

        member.updateProfile(memberUpdateRequest.getNickname(), savedFileName);
        memberService.save(member);

        String preSignedUrl = s3FileService.getPutPreSignedUrl("profiles", savedFileName, imageExtension, contentLength);

        return ResponseEntity.status(HttpStatus.OK).body(preSignedUrl);
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
