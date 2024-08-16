package com.habitpay.habitpay.domain.ChallengeAbsenceFee.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class FeeStatusResponse {
    private int totalFee;
    private int myFee;
    private List<MemberFeeResponse> memberFeeList;
}
