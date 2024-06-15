package com.habitpay.habitpay.domain.challenge.application;

import com.habitpay.habitpay.domain.challenge.dao.ChallengeRepository;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challenge.dto.ChallengeCreationRequest;
import com.habitpay.habitpay.domain.member.application.MemberService;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.global.common.response.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ChallengeCreationService {
    private final MemberService memberService;
    private final ChallengeRepository challengeRepository;

    @Transactional
    public ResponseEntity<ApiResponse> save(ChallengeCreationRequest challengeCreationRequest, String email) {
        Member host = memberService.findByEmail(email);
        Challenge challenge = Challenge.builder()
                .member(host)
                .title(challengeCreationRequest.getTitle())
                .description(challengeCreationRequest.getDescription())
                .startDate(challengeCreationRequest.getStartDate())
                .endDate(challengeCreationRequest.getEndDate())
                .participatingDays(challengeCreationRequest.getParticipatingDays())
                .feePerAbsence(challengeCreationRequest.getFeePerAbsence())
                .build();
        challengeRepository.save(challenge);
        
        // TODO: 챌린지 id 도 함께 전달하기
        ApiResponse apiResponse = ApiResponse.create("챌린지가 생성되었습니다.");
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
}
