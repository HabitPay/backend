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
public class ChallengeParticipationRecordService {

    private final ChallengeParticipationRecordRepository challengeParticipationRecordRepository;

    public ChallengeParticipationRecord save(ChallengeEnrollment enrollment, ChallengePost post) {
        // todo : 챌린지 조건 따져서 인정될 때만 저장해야 함
        // todo : enrollment service에서 enrollment의 성공 횟수를 +1하는 메서드 만들고, 여기에 넣어야 할 듯
        return challengeParticipationRecordRepository.save(
                ChallengeParticipationRecord.builder()
                        .enrollment(enrollment)
                        .post(post)
                        .build());
    }
}