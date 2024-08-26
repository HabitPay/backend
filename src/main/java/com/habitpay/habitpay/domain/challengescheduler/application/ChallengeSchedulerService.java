package com.habitpay.habitpay.domain.challengescheduler.application;

import com.habitpay.habitpay.domain.challenge.dao.ChallengeRepository;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challenge.domain.ChallengeState;
import com.habitpay.habitpay.domain.challengeparticipationrecord.dao.ChallengeParticipationRecordRepository;
import com.habitpay.habitpay.domain.challengeparticipationrecord.domain.ChallengeParticipationRecord;
import com.habitpay.habitpay.domain.participationstat.dao.ParticipationStatRepository;
import com.habitpay.habitpay.domain.participationstat.domain.ParticipationStat;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void setChallengeStateForStart() {
        // 오늘 날짜 시작인 챌린지 리스트 얻기
        // 챌린지 리스트를 state 진행 중으로 바꾸기
        // 각 챌린지 내의 멤버를 위한 참여 목록 한 번에 모두! 만들기

        // 매일 자정에 검사
        // 시작 날짜 요일과 오늘 요일이 동일한 경우
        // 1주일 치 참여 목록 만들기
        // 챌린지 생성일이 시작 요일과 동일한 경우만 참여 목록 1주일치 생성하는 예외 처리 필요
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void setChallengeStateForEnd() {}

    @Transactional
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void checkParticipationForChallenge() {

        ZonedDateTime yesterday = ZonedDateTime.now().minusDays(1);
        DayOfWeek yesterdayOfWeek = yesterday.getDayOfWeek();
        byte yesterdayBitPosition = (byte) ((byte) 1 << (7 - yesterdayOfWeek.getValue()));

        List<Challenge> challengeList = challengeRepository
                .findAllByStateAndParticipatingDays(ChallengeState.IN_PROGRESS, yesterdayBitPosition);
        if (challengeList.isEmpty()) { return; }

        LocalDate targetDate = yesterday.toLocalDate();
        List<ParticipationStat> failStatList = new ArrayList<>();
        List<ChallengeParticipationRecord> failRecordList = new ArrayList<>();

        challengeParticipationRecordRepository.findRecordCheckDTOByChallengeInAndTargetDate(challengeList, targetDate)
                .forEach(recordDto -> {
                    if (!recordDto.getRecord().existChallengePost()) {
                        recordDto.getStat().setFailureCountAndTotalFee(recordDto.getFeePerAbsence());
                        failStatList.add(recordDto.getStat());
                        failRecordList.add(recordDto.getRecord());
                    }
                });

        participationStatRepository.saveAll(failStatList);
        challengeParticipationRecordRepository.deleteAll(failRecordList);
    }
}
