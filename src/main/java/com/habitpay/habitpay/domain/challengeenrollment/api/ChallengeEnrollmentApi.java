package com.habitpay.habitpay.domain.challengeenrollment.api;

import com.habitpay.habitpay.domain.challengeenrollment.application.ChallengeEnrollmentService;
import com.habitpay.habitpay.domain.member.application.MemberService;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.global.config.jwt.TokenService;
import com.habitpay.habitpay.global.error.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChallengeEnrollmentApi {
    private final MemberService memberService;
    private final TokenService tokenService;
    private final ChallengeEnrollmentService challengeEnrollmentService;

    @PostMapping("/challenges/{id}/enroll")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> enrollChallenge(@PathVariable("id") Long id, @RequestHeader("Authorization") String authorizationHeader) {

        // TODO: Interceptor 나 Filter 에서 먼저 처리해주기 때문에 나중에 삭제하기
        Optional<String> optionalToken = tokenService.getTokenFromHeader(authorizationHeader);
        if (optionalToken.isEmpty()) {
            String message = ErrorResponse.UNAUTHORIZED.getMessage();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
        }

        String token = optionalToken.get();
        String email = tokenService.getEmail(token);
        Member member = memberService.findByEmail(email);
        log.info("[POST /challenges/{}/enroll] email: {}", id, email);
        return challengeEnrollmentService.enroll(id, member);
    }

}
