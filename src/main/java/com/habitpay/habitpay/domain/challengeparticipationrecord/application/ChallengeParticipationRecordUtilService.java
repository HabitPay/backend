package com.habitpay.habitpay.domain.challengeparticipationrecord.application;

import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengeparticipationrecord.dao.ChallengeParticipationRecordRepository;
import com.habitpay.habitpay.domain.challengeparticipationrecord.domain.ChallengeParticipationRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class ChallengeParticipationRecordUtilService {

    private final ChallengeParticipationRecordRepository challengeParticipationRecordRepository;

    public boolean getIsParticipatedToday(ChallengeEnrollment enrollment) {
        ZonedDateTime startOfToday = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("Asia/Seoul")).with(LocalTime.MIDNIGHT);

        return challengeParticipationRecordRepository
                .findByChallengeEnrollmentAndTargetDate(enrollment, startOfToday)
                .map(ChallengeParticipationRecord::existChallengePost)
                .orElseGet(() -> false);
    }
}
