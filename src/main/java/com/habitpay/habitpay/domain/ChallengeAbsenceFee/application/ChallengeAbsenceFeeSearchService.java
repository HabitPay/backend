package com.habitpay.habitpay.domain.ChallengeAbsenceFee.application;

import com.habitpay.habitpay.domain.challenge.application.ChallengeSearchService;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.application.ChallengeEnrollmentSearchService;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengeenrollment.exception.NotEnrolledChallengeException;
import com.habitpay.habitpay.domain.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeAbsenceFeeSearchService {

    private final ChallengeEnrollmentSearchService challengeEnrollmentSearchService;

//    public int findPersonalTotalFeeOfChallenge(Member member, Challenge challenge) {
//        Optional<ChallengeEnrollment> optionalEnrollment = challengeEnrollmentSearchService.findByMemberAndChallenge(member, challenge);
//        return optionalEnrollment.map(ChallengeEnrollment::getTotalFee)
//                .orElseThrow(() -> new NotEnrolledChallengeException(member.getId(), challenge.getId()));
//    }

    // todo: challenge를 받아서, 챌린지에 등록한 enrollment list 기준으로 fee를 담은 DTO 만들어도 될 듯함
//    public int findPersonalTotalFeeOfChallenge(ChallengeEnrollment enrollment) {
//        return enrollment.getTotalFee();
//    }

    // todo: 역시 위의 새로운 방식을 기준으로 해서 fee의 합을 반환해도 될 듯함 (정합성에도 좋음)
    //       그 경우 challenge의 totalFee 속성 없애기
//    public int findTotalFeeOfChallenge(Challenge challenge) {
//        return challenge.getTotalAbsenceFee();
//    }
}
