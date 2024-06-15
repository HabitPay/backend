package com.habitpay.habitpay.domain.challenge.application;

import com.habitpay.habitpay.domain.challenge.dao.ChallengeRepository;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challenge.dto.ChallengePatchRequest;
import com.habitpay.habitpay.domain.member.application.MemberService;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChallengeUpdateService {
    private final MemberService memberService;
    private final ChallengeRepository challengeRepository;
    private final ChallengeSearchService challengeSearchService;

    @Transactional
    public ResponseEntity<ApiResponse> update(Long id, ChallengePatchRequest challengePatchRequest, String email) {
        ApiResponse apiResponse;
        Member member = memberService.findByEmail(email);
        Challenge challenge = challengeSearchService.findById(id);

        if (isChallengeHost(member, challenge) == false) {
            // TODO: throw 로 예외 처리 가능한지 확인하기
            apiResponse = ApiResponse.create("챌린지 수정 권한이 없습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
        }

        challenge.updateDescription(challengePatchRequest.getDescription());
        challengeRepository.save(challenge);

        apiResponse = ApiResponse.create("챌린지 정보 수정이 반영되었습니다.");
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    private boolean isChallengeHost(Member member, Challenge challenge) {
        return member.getEmail().equals(challenge.getHost().getEmail());
    }
}
