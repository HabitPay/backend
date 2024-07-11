package com.habitpay.habitpay.domain.ChallengeAbsenceFee.application;

import com.habitpay.habitpay.domain.challenge.application.ChallengeSearchService;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.application.ChallengeEnrollmentSearchService;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeAbsenceFeeSearchService {

    private final ChallengeEnrollmentSearchService challengeEnrollmentSearchService;

    public int findPersonalTotalFeeOfChallenge(Member member, Challenge challenge) {
        Optional<ChallengeEnrollment> optionalEnrollment = challengeEnrollmentSearchService.findByMemberAndChallenge(member, challenge);
        return optionalEnrollment.map(ChallengeEnrollment::getTotalFee).orElse(0);
    }

    public int findPersonalTotalFeeOfChallenge(ChallengeEnrollment enrollment) {
        return enrollment.getTotalFee();
    }

    public int findTotalFeeOfChallenge(Challenge challenge) {
        return challenge.getTotalAbsenceFee();
    }
}
