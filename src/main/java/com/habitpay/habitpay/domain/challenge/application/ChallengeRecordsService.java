package com.habitpay.habitpay.domain.challenge.application;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challenge.dto.ChallengeRecordsResponse;
import com.habitpay.habitpay.domain.challengeenrollment.application.ChallengeEnrollmentSearchService;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.global.response.SuccessCode;
import com.habitpay.habitpay.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChallengeRecordsService {

    private final ChallengeSearchService challengeSearchService;
    private final ChallengeEnrollmentSearchService challengeEnrollmentSearchService;

    public SuccessResponse<ChallengeRecordsResponse> getChallengeRecords(Long challengeId, Member member) {
        Challenge challenge = challengeSearchService.getChallengeById(challengeId);
        ChallengeEnrollment enrollment = challengeEnrollmentSearchService.getByMemberAndChallenge(member, challenge);
        List<ZonedDateTime> requiredParticipationDates = challenge.getParticipationDates();

        List<ZonedDateTime> successDayList = new ArrayList<>();
        List<ZonedDateTime> failDayList = new ArrayList<>();
        List<ZonedDateTime> upcomingDayList = new ArrayList<>();

        return SuccessResponse.of(
                SuccessCode.NO_MESSAGE,
                ChallengeRecordsResponse.builder()
                        .successDayList(successDayList)
                        .failDayList(failDayList)
                        .upcomingDayList(upcomingDayList)
                        .build()
        );
    }
}
