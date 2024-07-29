package com.habitpay.habitpay.domain.challengeparticipationrecord.application;

import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengeparticipationrecord.dao.ChallengeParticipationRecordRepository;
import com.habitpay.habitpay.domain.challengeparticipationrecord.domain.ChallengeParticipationRecord;
import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeParticipationRecordCreationService {

    private final ChallengeParticipationRecordRepository challengeParticipationRecordRepository;

    public ChallengeParticipationRecord save(ChallengeEnrollment enrollment, ChallengePost post) {

        ChallengeParticipationRecord record =
                challengeParticipationRecordRepository.save(
                        ChallengeParticipationRecord.builder()
                                .enrollment(enrollment)
                                .post(post)
                                .build());

        enrollment.plusSuccessCountWithParticipationRecord(record);
        return record;
    }
}