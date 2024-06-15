package com.habitpay.habitpay.domain.challenge.api;

import com.habitpay.habitpay.domain.challenge.application.ChallengeCreationService;
import com.habitpay.habitpay.domain.challenge.application.ChallengeDetailService;
import com.habitpay.habitpay.domain.challenge.application.ChallengeSearchService;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challenge.dto.ChallengeCreationRequest;
import com.habitpay.habitpay.domain.challenge.dto.ChallengePatchRequest;
import com.habitpay.habitpay.domain.challenge.dto.ChallengeResponse;
import com.habitpay.habitpay.domain.member.application.MemberService;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.global.config.jwt.TokenService;
import com.habitpay.habitpay.global.error.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChallengeApi {
    private final ChallengeCreationService challengeCreationService;
    private final ChallengeSearchService challengeSearchService;
    private final ChallengeDetailService challengeDetailService;
    private final MemberService memberService;
    private final TokenService tokenService;

    @GetMapping("/challenges/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ChallengeResponse> getChallengeDetail(@PathVariable("id") Long id,
                                                                @AuthenticationPrincipal String email) {
        log.info("[GET /challenges/{}]", id);
        return challengeDetailService.getChallengeDetailById(id, email);
    }

    @PostMapping("/challenges")
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
        log.info("[POST /challenges] email: {}", email);

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

    @PatchMapping("/challenges/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> patchChallengeDetails(@PathVariable("id") Long id, @RequestBody ChallengePatchRequest challengePatchRequest,
                                                   @RequestHeader("Authorization") String authorizationHeader) {
        log.info("[PATCH /challenges/{}]", id);

        Optional<String> optionalToken = tokenService.getTokenFromHeader(authorizationHeader);
        String token = optionalToken.get();
        String email = tokenService.getEmail(token);
        Member member = memberService.findByEmail(email);
        Challenge challenge = challengeSearchService.findById(id);

        if (member.getEmail().equals(challenge.getHost().getEmail()) == false) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("챌린지 수정 권한이 없습니다.");
        }

        challenge.updateChallengeDescription(challengePatchRequest.getDescription());
        challengeCreationService.save(challenge);

        return ResponseEntity.status(HttpStatus.OK).body("챌린지 정보 수정이 반영되었습니다.");
    }
}
