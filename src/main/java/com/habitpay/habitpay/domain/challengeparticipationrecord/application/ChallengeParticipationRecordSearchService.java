package com.habitpay.habitpay.domain.challengeparticipationrecord.application;

import com.habitpay.habitpay.domain.challengeparticipationrecord.dao.ChallengeParticipationRecordRepository;
import com.habitpay.habitpay.domain.challengeparticipationrecord.domain.ChallengeParticipationRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeParticipationRecordSearchService {

    private final ChallengeParticipationRecordRepository challengeParticipationRecordRepository;

    public ChallengeParticipationRecord findById(Long id) {
        return challengeParticipationRecordRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No Such Record " + id));
    }
}
