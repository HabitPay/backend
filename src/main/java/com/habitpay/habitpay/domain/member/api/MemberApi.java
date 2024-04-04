package com.habitpay.habitpay.domain.member.api;

import com.google.gson.JsonObject;
import com.habitpay.habitpay.domain.member.application.MemberService;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.member.dto.MemberRequest;
import com.habitpay.habitpay.global.config.aws.S3FileService;
import com.habitpay.habitpay.global.config.jwt.TokenService;
import com.habitpay.habitpay.global.error.ErrorResponse;
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
    private final TokenService tokenService;
    private final S3FileService s3FileService;

    @PostMapping("/member")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> activateMember(@RequestBody MemberRequest memberRequest,
                                                 @RequestHeader("Authorization") String authorizationHeader) {

        // TODO: Interceptor 나 Filter 에서 먼저 처리해주기 때문에 나중에 삭제하기
        Optional<String> optionalToken = tokenService.getTokenFromHeader(authorizationHeader);
        if (optionalToken.isEmpty()) {
            String message = ErrorResponse.UNAUTHORIZED.getMessage();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
        }

        String token = optionalToken.get();
        log.info("[POST /member] token: {}", token);

        String email = tokenService.getEmail(token);
        String nickname = memberRequest.getNickname();
        log.info("[POST /member] email: {}, nickname: {}", email, nickname);

        Member member = memberService.findByEmail(email);

        log.info("[POST /member] 회원 조회 성공");

        member.activate(nickname);
        memberService.save(member);

        log.info("[POST /member] 회원 활성화 성공");

        String newToken = tokenService.createAccessToken(email);
        JsonObject responseBody = new JsonObject();
        responseBody.addProperty("accessToken", newToken);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody.toString());

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

        String imageExtension = memberRequest.getImageExtension();
        log.info("[PATCH /member] imageExtension: {}", imageExtension);
        if (ImageUtil.isValidImageExtension(imageExtension) == false) {
            String message = ErrorResponse.UNSUPPORTED_IMAGE_EXTENSION.getMessage();
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(message);
        }

        log.info("[PATCH /member] nickname: {}", memberRequest.getNickname());


        String randomFileName = UUID.randomUUID().toString();
        log.info("[PATCH /member] randomFileName: {}", randomFileName);

        String token = optionalToken.get();
        String email = tokenService.getEmail(token);
        Member member = memberService.findByEmail(email);
        member.updateProfile(memberRequest.getNickname(), randomFileName);
        memberService.save(member);

        String preSignedUrl = s3FileService.getPutPreSignedUrl("profiles", randomFileName);
        return ResponseEntity.status(HttpStatus.OK).body(preSignedUrl);
    }

}
