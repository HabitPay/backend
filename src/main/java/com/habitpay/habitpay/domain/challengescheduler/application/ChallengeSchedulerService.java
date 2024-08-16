package com.habitpay.habitpay.domain.challengescheduler.application;

import com.habitpay.habitpay.domain.challenge.dao.ChallengeRepository;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challenge.domain.ChallengeState;
import com.habitpay.habitpay.domain.challengeparticipationrecord.dao.ChallengeParticipationRecordRepository;
import com.habitpay.habitpay.domain.participationstat.dao.ParticipationStatRepository;
import com.habitpay.habitpay.domain.participationstat.domain.ParticipationStat;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChallengeSchedulerService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeParticipationRecordRepository challengeParticipationRecordRepository;
    private final ParticipationStatRepository participationStatRepository;

    // todo : Challenge 시작, 종료 시 state 변경 등 메서드

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void checkParticipationForChallenge() {

        ZonedDateTime yesterday = ZonedDateTime.now().minusDays(1);
        DayOfWeek yesterdayOfWeek = yesterday.getDayOfWeek();
        byte yesterdayBitPosition = (byte) ((byte) 1 << (7 - yesterdayOfWeek.getValue()));

        List<Challenge> challengeList = challengeRepository
                .findAllByStateAndParticipatingDays(ChallengeState.IN_PROGRESS, yesterdayBitPosition);
        if (challengeList.isEmpty()) { return; }

        LocalDate targetDate = yesterday.toLocalDate();
        List<ParticipationStat> statList = new ArrayList<>();
        challengeParticipationRecordRepository.findRecordCheckDTOByChallengeInAndTargetDate(challengeList, targetDate)
                .forEach(recordDto -> {
                    if (!recordDto.getRecord().existChallengePost()) {
                        recordDto.getStat().setFailureCountAndTotalFee(recordDto.getFeePerAbsence());
                        statList.add(recordDto.getStat());
                    }
                });

        participationStatRepository.saveAll(statList);
    }
}
