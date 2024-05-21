package com.habitpay.habitpay.domain.challenge.api;

import com.habitpay.habitpay.domain.challenge.application.ChallengeCreateService;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challenge.dto.ChallengeRequest;
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

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChallengeApi {
    private final ChallengeCreateService challengeCreateService;
    private final MemberService memberService;
    private final TokenService tokenService;

    @PostMapping("/challenge")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createChallenge(@RequestBody ChallengeRequest challengeRequest,
                                             @RequestHeader("Authorization") String authorizationHeader) {

        // TODO: Interceptor 나 Filter 에서 먼저 처리해주기 때문에 나중에 삭제하기
        Optional<String> optionalToken = tokenService.getTokenFromHeader(authorizationHeader);
        if (optionalToken.isEmpty()) {
            String message = ErrorResponse.UNAUTHORIZED.getMessage();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
        }

        String token = optionalToken.get();
        String email = tokenService.getEmail(token);
        Member member = memberService.findByEmail(email);
        log.info("[POST /challenge] email: {}", email);

        Challenge challenge = Challenge.builder()
                .member(member)
                .title(challengeRequest.getTitle())
                .description(challengeRequest.getDescription())
                .startDate(challengeRequest.getStartDate())
                .endDate(challengeRequest.getEndDate())
                .participatingDays(challengeRequest.getParticipatingDays())
                .feePerAbsence(challengeRequest.getFeePerAbsence())
                .build();

        challengeCreateService.save(challenge);

        return ResponseEntity.status(HttpStatus.CREATED).body(challenge.getId());
    }
}
