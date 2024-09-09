package com.habitpay.habitpay.domain.challengescheduler.application;

import com.habitpay.habitpay.domain.challenge.dao.ChallengeRepository;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challenge.domain.ChallengeState;
import com.habitpay.habitpay.domain.challengeparticipationrecord.application.ChallengeParticipationRecordSearchService;
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
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChallengeSchedulerService {

    private final ChallengeRepository challengeRepository;
    private final ParticipationStatRepository participationStatRepository;
    private final ChallengeParticipationRecordRepository challengeParticipationRecordRepository;
    private final ChallengeParticipationRecordSearchService challengeParticipationRecordSearchService;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void setChallengeForStart() {
        // 오늘 날짜 시작인 챌린지 리스트 얻기 -> todo : 챌린지의 시작 날짜를 zoneDateTime이 아닌 날짜로 바꾼 후에 repository에서 날짜 검색 메서드 추가하기
        // 챌린지 리스트를 state 진행 중으로 바꾸기
        // -----------------

        // 각 챌린지 내의 멤버 리스트 얻기
        // 멤버 리스트 순회하면서 참여 목록 한 번에 모두! 만들기

        // 챌린지 생성일이 시작 요일과 동일한 경우만 참여 목록 따로 생성하는 예외 처리 필요
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void setChallengeForEnd() {}

    @Transactional
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void checkParticipationForChallenge() {

        ZonedDateTime yesterday = ZonedDateTime.now().minusDays(1);
        DayOfWeek yesterdayOfWeek = yesterday.getDayOfWeek();
        byte yesterdayBitPosition = (byte) ((byte) 1 << (7 - yesterdayOfWeek.getValue()));

        List<Challenge> challengeList = challengeRepository
                .findAllByStateAndParticipatingDays(ChallengeState.IN_PROGRESS, yesterdayBitPosition);
        if (challengeList.isEmpty()) { return; }

        ZonedDateTime startOfTargetDate = yesterday.with(LocalTime.MIDNIGHT);
        List<ParticipationStat> failStatList = new ArrayList<>();
        List<ChallengeParticipationRecord> failRecordList = new ArrayList<>();

        challengeParticipationRecordSearchService.findByChallengesAndTargetDate(challengeList, startOfTargetDate)
                .forEach(record -> {
                    if (!record.existChallengePost()) {
                        ParticipationStat stat = record.getParticipationStat();
                        stat.setFailureCount(stat.getFailureCount() + 1);
                        stat.setTotalFee(stat.getTotalFee() + record.getChallenge().getFeePerAbsence());
                        failStatList.add(record.getParticipationStat());
                        failRecordList.add(record);
                    }
                });

        participationStatRepository.saveAll(failStatList);
        challengeParticipationRecordRepository.deleteAll(failRecordList);
    }
}
