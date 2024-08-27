package com.habitpay.habitpay.domain.challengeabsencefee.application;

import com.habitpay.habitpay.domain.challengeabsencefee.dto.FeeStatusResponse;
import com.habitpay.habitpay.domain.challengeabsencefee.dto.MemberFeeResponse;
import com.habitpay.habitpay.domain.challengeabsencefee.exception.DaysCountException;
import com.habitpay.habitpay.domain.challenge.application.ChallengeSearchService;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.application.ChallengeEnrollmentSearchService;
import com.habitpay.habitpay.domain.challengeenrollment.dao.ChallengeEnrollmentRepository;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.global.response.SuccessCode;
import com.habitpay.habitpay.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeAbsenceFeeSearchService {

    private final ChallengeSearchService challengeSearchService;
    private final ChallengeEnrollmentSearchService challengeEnrollmentSearchService;
    private final ChallengeEnrollmentRepository challengeEnrollmentRepository;

    public SuccessResponse<FeeStatusResponse> makeMemberFeeDataListOfChallenge(Long challengeId, Member member) {
        Challenge challenge = challengeSearchService.getChallengeById(challengeId);
        ChallengeEnrollment enrollment = challengeEnrollmentSearchService.getByMemberAndChallenge(member, challenge);

        int totalParticipatingDaysCount = challenge.getTotalParticipatingDaysCount();
        if (totalParticipatingDaysCount == 0) {throw new DaysCountException(totalParticipatingDaysCount); }

        List<MemberFeeResponse> memberFeeResponseList = challengeEnrollmentRepository
                .findMemberFeeDTOByChallenge(challenge)
                .stream()
                .map(memberFeeDto -> MemberFeeResponse.of(memberFeeDto, totalParticipatingDaysCount))
                .toList();

        FeeStatusResponse feeStatusResponse = FeeStatusResponse.builder()
                .totalFee(findTotalFeeOfChallenge(memberFeeResponseList))
                .myFee(findPersonalTotalFeeOfChallenge(enrollment))
                .memberFeeList(memberFeeResponseList)
                .build();

        return SuccessResponse.of(
                SuccessCode.NO_MESSAGE,
                feeStatusResponse
        );
    }

    private static int findPersonalTotalFeeOfChallenge(ChallengeEnrollment enrollment) {
        return enrollment.getParticipationStat().getTotalFee();
    }

    private static int findTotalFeeOfChallenge(List<MemberFeeResponse> memberFeeList) {
        return memberFeeList.stream().mapToInt(MemberFeeResponse::getTotalFee).sum();
    }
}
