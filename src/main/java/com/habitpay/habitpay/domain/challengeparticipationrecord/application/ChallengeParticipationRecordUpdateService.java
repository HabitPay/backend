package com.habitpay.habitpay.domain.challengeparticipationrecord.application;

import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengeparticipationrecord.dao.ChallengeParticipationRecordRepository;
import com.habitpay.habitpay.domain.challengeparticipationrecord.domain.ChallengeParticipationRecord;
import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.participationstat.dao.ParticipationStatRepository;
import com.habitpay.habitpay.domain.participationstat.domain.ParticipationStat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeParticipationRecordUpdateService {

    private final ChallengeParticipationRecordRepository challengeParticipationRecordRepository;
    private final ParticipationStatRepository participationStatRepository;
    private final ChallengeParticipationRecordSearchService challengeParticipationRecordSearchService;

    public void setChallengePost(
            ChallengeParticipationRecord record,
            ChallengePost post) {

        record.setChallengePost(post);
        challengeParticipationRecordRepository.save(record);

        ParticipationStat stat = record.getParticipationStat();
        stat.setSuccessCount(stat.getSuccessCount() + 1);
        participationStatRepository.save(stat);

    }
}