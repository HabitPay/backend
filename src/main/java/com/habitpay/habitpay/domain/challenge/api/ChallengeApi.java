package com.habitpay.habitpay.domain.challenge.api;

import com.habitpay.habitpay.domain.challenge.application.ChallengeCreationService;
import com.habitpay.habitpay.domain.challenge.application.ChallengeSearchService;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challenge.dto.ChallengeCreationRequest;
import com.habitpay.habitpay.domain.challenge.dto.ChallengeResponse;
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
    private final ChallengeCreationService challengeCreationService;
    private final ChallengeSearchService challengeSearchService;
    private final MemberService memberService;
    private final TokenService tokenService;

    @PostMapping("/challenge")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createChallenge(@RequestBody ChallengeCreationRequest challengeCreationRequest,
                                             @RequestHeader("Authorization") String authorizationHeader) {

        // TODO: Interceptor 나 Filter 에서 먼저 처리해주기 때문에 나중에 삭제하기
        Optional<String> optionalToken = tokenService.getTokenFromHeader(authorizationHeader);
        if (optionalToken.isEmpty()) {
            String message = ErrorResponse.UNAUTHORIZED.getMessage();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
        }

        String token = optionalToken.get();
        String email = tokenService.getEmail(token);
        Member host = memberService.findByEmail(email);
        log.info("[POST /challenge] email: {}", email);

        Challenge challenge = Challenge.builder()
                .member(host)
                .title(challengeCreationRequest.getTitle())
                .description(challengeCreationRequest.getDescription())
                .startDate(challengeCreationRequest.getStartDate())
                .endDate(challengeCreationRequest.getEndDate())
                .participatingDays(challengeCreationRequest.getParticipatingDays())
                .feePerAbsence(challengeCreationRequest.getFeePerAbsence())
                .build();

        challengeCreationService.save(challenge);

        return ResponseEntity.status(HttpStatus.CREATED).body(challenge.getId());
    }

    @GetMapping("/challenge/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getChallenge(@PathVariable("id") Long id,
                                          @RequestHeader("Authorization") String authorizationHeader) {

        log.info("[GET /challenge] id: {}", id);

        // TODO: Interceptor 나 Filter 에서 먼저 처리해주기 때문에 나중에 삭제하기
        Optional<String> optionalToken = tokenService.getTokenFromHeader(authorizationHeader);
        String token = optionalToken.get();
        String email = tokenService.getEmail(token);
        Member host = memberService.findByEmail(email);

        // TODO: 사용자의 Challenge 등록 여부를 확인한 후 return 하기

        Challenge challenge = challengeSearchService.findById(id);
        ChallengeResponse challengeResponse = new ChallengeResponse(host, challenge);

        return ResponseEntity.status(HttpStatus.OK).body(challengeResponse);
    }
}
