package com.habitpay.habitpay.domain.challengeparticipationrecord.application;

import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengeparticipationrecord.dao.ChallengeParticipationRecordRepository;
import com.habitpay.habitpay.domain.challengeparticipationrecord.domain.ChallengeParticipationRecord;
import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeParticipationRecordUpdateService {

    private final ChallengeParticipationRecordRepository challengeParticipationRecordRepository;
    private final ChallengeParticipationRecordSearchService challengeParticipationRecordSearchService;

    public ChallengeParticipationRecord setChallengePost(
            ChallengeEnrollment enrollment,
            LocalDate today,
            ChallengePost post) {

        ChallengeParticipationRecord record = challengeParticipationRecordSearchService
                .findByChallengeEnrollmentAndTargetDate(enrollment, today);
        record.setChallengePost(post);
        challengeParticipationRecordRepository.save(record);

        record.getParticipationStat().setSuccessCount();
        return record;
    }
}