package com.habitpay.habitpay.domain.member.api;

import com.google.gson.JsonObject;
import com.habitpay.habitpay.domain.member.application.MemberService;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.member.dto.MemberRequest;
import com.habitpay.habitpay.global.config.jwt.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberApi {
    private final MemberService memberService;
    private final TokenService tokenService;

    @PostMapping("/member")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> activateMember(@RequestBody MemberRequest memberRequest,
                                                 @RequestHeader("Authorization") String authorizationHeader) {

        // todo: 예외처리 추가하기
//        if (authorizationHeader == null || authorizationHeader.startsWith("Bearer") {
//
//        }

        StringTokenizer tokenizer = new StringTokenizer(authorizationHeader);
        if (tokenizer.countTokens() == 2 && tokenizer.nextToken().equals("Bearer")) {
            String token = tokenizer.nextToken();
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

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("올바른 접근이 아닙니다.");
    }

    @PatchMapping("/member")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> patchMember(@ModelAttribute MemberRequest memberRequest) throws IOException {
        log.info("[PATCH /member] nickname: {}", memberRequest.getNickname());
        if (memberRequest.getProfileImage() != null) {
            MultipartFile profileImage = memberRequest.getProfileImage();
            String fileName = profileImage.getOriginalFilename();
            log.info("[PATCH /member] fileName: {}", fileName);
            String filePath = "/Users/joonhyuk/workspace/habitpay/HabitPay/backend/images/" + fileName;
            log.info("[PATCH /member] filePath: {}", filePath);
            profileImage.transferTo(new File(filePath));
        }
        return ResponseEntity.status(HttpStatus.OK).body("Test");
    }
}
