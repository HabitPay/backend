package com.habitpay.habitpay.domain.member.api;

import com.habitpay.habitpay.domain.member.application.MemberProfileService;
import com.habitpay.habitpay.domain.member.application.MemberService;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.member.dto.MemberRequest;
import com.habitpay.habitpay.domain.member.dto.MemberResponse;
import com.habitpay.habitpay.domain.model.Response;
import com.habitpay.habitpay.domain.refreshtoken.application.RefreshTokenService;
import com.habitpay.habitpay.domain.refreshtoken.dto.CreateAccessTokenResponse;
import com.habitpay.habitpay.global.config.aws.S3FileService;
import com.habitpay.habitpay.global.config.jwt.TokenService;
import com.habitpay.habitpay.global.error.CustomJwtErrorInfo;
import com.habitpay.habitpay.global.error.ErrorResponse;
import com.habitpay.habitpay.domain.refreshtoken.exception.CustomJwtException;
import com.habitpay.habitpay.global.util.ImageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberApi {
    private final MemberService memberService;
    private final MemberProfileService memberProfileService;
    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;
    private final S3FileService s3FileService;

    @GetMapping("/member")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getMember(@RequestHeader("Authorization") String authorizationHeader) {
        Optional<String> optionalToken = tokenService.getTokenFromHeader(authorizationHeader);
        if (optionalToken.isEmpty()) {
            String message = ErrorResponse.UNAUTHORIZED.getMessage();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
        }

        String token = optionalToken.get();
        log.info("[GET /member] token: {}", token);

        String email = tokenService.getEmail(token);
        log.info("[GET /member] email: {}", email);

        Member member = memberService.findByEmail(email);
        String nickname = member.getNickname();
        Optional<String> optionalImageFileName = Optional.ofNullable(member.getImageFileName());
        MemberResponse memberResponse;

        if (optionalImageFileName.isEmpty()) {
            memberResponse = new MemberResponse(nickname, "");
        } else {
            String imageFileName = optionalImageFileName.get();
            log.info("[GET /member] imageFileName: {}", imageFileName);
            String preSignedGetUrl = s3FileService.getGetPreSignedUrl("profiles", imageFileName);
            memberResponse = new MemberResponse(nickname, preSignedGetUrl);
        }

        return ResponseEntity.status(HttpStatus.OK).body(memberResponse);
    }

    @PostMapping("/member")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CreateAccessTokenResponse> activateMember(
            @RequestBody MemberRequest memberRequest,
            @RequestHeader("Authorization") String authorizationHeader) {

        // TODO: Interceptor 나 Filter 에서 먼저 처리해주기 때문에 나중에 삭제하기 -> 보류
        Optional<String> optionalToken = tokenService.getTokenFromHeader(authorizationHeader);
        if (optionalToken.isEmpty()) {
            String message = ErrorResponse.UNAUTHORIZED.getMessage();
            throw new CustomJwtException(HttpStatus.UNAUTHORIZED, CustomJwtErrorInfo.UNAUTHORIZED, message);
        }

        String token = optionalToken.get();
        log.info("[POST /member] token: {}", token);

        String email = tokenService.getEmail(token);
        String nickname = memberRequest.getNickname();
        log.info("[POST /member] email: {}, nickname: {}", email, nickname);
        if (!memberProfileService.isValidNickname(nickname)) {
            String message = ErrorResponse.INVALID_NICKNAME_RULE.getMessage();
//            todo : CustomJwtErrorInfo 바꾸거나 추가하기
            throw new CustomJwtException(HttpStatus.UNPROCESSABLE_ENTITY, CustomJwtErrorInfo.BAD_REQUEST, message);
        }

        Member member = memberService.findByEmail(email);

        log.info("[POST /member] 회원 조회 성공");

        member.activate(nickname);
        memberService.save(member);

        log.info("[POST /member] 회원 활성화 성공");

        String newToken = tokenService.createAccessToken(email);
        String refreshToken = refreshTokenService.setRefreshTokenByEmail(email);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateAccessTokenResponse(
                        newToken,
                        "Bearer",
                        tokenService.getAccessTokenExpiresInToMillis(),
                        refreshToken));
    }

    @PatchMapping("/member")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> patchMember(@RequestBody MemberRequest memberRequest,
                                              @RequestHeader("Authorization") String authorizationHeader) {
        // TODO: Interceptor 나 Filter 에서 먼저 처리해주기 때문에 나중에 삭제하기
        Optional<String> optionalToken = tokenService.getTokenFromHeader(authorizationHeader);
        if (optionalToken.isEmpty()) {
            String message = ErrorResponse.UNAUTHORIZED.getMessage();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
        }
        String nickname = memberRequest.getNickname();
        String imageExtension = memberRequest.getImageExtension();
        log.info("[PATCH /member] nickname: {}, imageExtension: {}", nickname, imageExtension);

        String token = optionalToken.get();
        String email = tokenService.getEmail(token);
        Member member = memberService.findByEmail(email);

        Long contentLength = memberRequest.getContentLength();

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
        if (memberProfileService.isValidNickname(nickname) == false) {
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

        member.updateProfile(memberRequest.getNickname(), savedFileName);
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
