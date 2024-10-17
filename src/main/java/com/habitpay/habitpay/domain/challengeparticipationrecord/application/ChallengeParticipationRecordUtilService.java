package com.habitpay.habitpay.domain.challengeparticipationrecord.application;

import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengeparticipationrecord.dao.ChallengeParticipationRecordRepository;
import com.habitpay.habitpay.domain.challengeparticipationrecord.domain.ChallengeParticipationRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class ChallengeParticipationRecordUtilService {

    private final ChallengeParticipationRecordRepository challengeParticipationRecordRepository;

    public boolean getIsParticipatedToday(ChallengeEnrollment enrollment, ZonedDateTime targetDay) {
        return challengeParticipationRecordRepository
                .findByChallengeEnrollmentAndTargetDate(enrollment, targetDay)
                .map(ChallengeParticipationRecord::existChallengePost)
                .orElseGet(() -> false);
    }
}
