package com.habitpay.habitpay.domain.challenge.application;

import com.habitpay.habitpay.global.response.SuccessCode;
import com.habitpay.habitpay.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChallengeMemberSearchService {

    public SuccessResponse<?> getEnrolledMemberList() {
        return SuccessResponse.of(
                SuccessCode.NO_MESSAGE
        );
    }
}
