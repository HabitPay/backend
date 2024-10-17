package com.habitpay.habitpay.domain.challenge.application;

import com.habitpay.habitpay.domain.challenge.dto.ChallengeRecordsResponse;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChallengeRecordsService {

    public SuccessResponse<ChallengeRecordsResponse> getChallengeRecords(Long challengeId, Member member) {}
}
