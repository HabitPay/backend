package com.habitpay.habitpay.domain.challenge.application;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challenge.dto.ChallengeRecords;
import com.habitpay.habitpay.domain.challenge.dto.ChallengeRecordsResponse;
import com.habitpay.habitpay.domain.challengeenrollment.application.ChallengeEnrollmentSearchService;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengeparticipationrecord.application.ChallengeParticipationRecordSearchService;
import com.habitpay.habitpay.domain.challengeparticipationrecord.domain.ChallengeParticipationRecord;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.global.config.timezone.TimeZoneConverter;
import com.habitpay.habitpay.global.response.SuccessCode;
import com.habitpay.habitpay.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChallengeRecordsService {

    private final ChallengeSearchService challengeSearchService;
    private final ChallengeEnrollmentSearchService challengeEnrollmentSearchService;
    private final ChallengeParticipationRecordSearchService challengeParticipationRecordSearchService;

    public SuccessResponse<ChallengeRecordsResponse> getChallengeRecords(Long challengeId, Member member) {
        Challenge challenge = challengeSearchService.getChallengeById(challengeId);
        ChallengeEnrollment enrollment = challengeEnrollmentSearchService.getByMemberAndChallenge(member, challenge);

        ChallengeRecords challengeRecords = new ChallengeRecords();
        List<ChallengeParticipationRecord> recordList = challengeParticipationRecordSearchService.findAllByChallengeEnrollment(enrollment);
        LocalDate today = TimeZoneConverter.convertEtcToLocalTimeZone(ZonedDateTime.now()).toLocalDate();

        recordList.forEach(record -> categorizeRecord(record, today, challengeRecords));

        return SuccessResponse.of(
                SuccessCode.NO_MESSAGE,
                ChallengeRecordsResponse.builder()
                        .successDayList(challengeRecords.getSuccessDayList())
                        .failDayList(challengeRecords.getFailDayList())
                        .upcomingDayList(challengeRecords.getUpcomingDayList())
                        .build()
        );
    }

    private void categorizeRecord(ChallengeParticipationRecord record, LocalDate today, ChallengeRecords challengeRecords) {
        LocalDate targetDate = TimeZoneConverter.convertEtcToLocalTimeZone(record.getTargetDate()).toLocalDate();

        if (targetDate.isBefore(today)) {
            if (record.existsChallengePost()) {
                challengeRecords.addSuccessDay(targetDate);
            } else {
                challengeRecords.addFailDay(targetDate);
            }
        } else if (targetDate.isEqual(today)) {
            if (record.existsChallengePost()) {
                challengeRecords.addSuccessDay(targetDate);
            } else {
                challengeRecords.addUpcomingDay(targetDate);
            }
        } else {
            challengeRecords.addUpcomingDay(targetDate);
        }
    }
}
