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
    public void setChallengeForStart() {
        // 오늘 날짜 시작인 챌린지 리스트 얻기 -> todo : 챌린지의 시작 날짜를 zoneDateTime이 아닌 날짜로 바꾼 후에 repository에서 날짜 검색 메서드 추가하기
        // 챌린지 리스트를 state 진행 중으로 바꾸기
        // -----------------

        // 각 챌린지 내의 멤버 리스트 얻기
        // 멤버 리스트 순회하면서 참여 목록 한 번에 모두! 만들기

        // 챌린지 생성일이 시작 요일과 동일한 경우만 참여 목록 1주일치 생성하는 예외 처리 필요
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void setChallengeForEnd() {}

    @Transactional
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void checkParticipationForChallenge() {

        ZonedDateTime yesterday = ZonedDateTime.now().minusDays(1);
        DayOfWeek yesterdayOfWeek = yesterday.getDayOfWeek();
        byte yesterdayBitPosition = (byte) ((byte) 1 << (7 - yesterdayOfWeek.getValue()));
        // 검사하고자 하는 날의 요일 비트 포지션을 구한다

        List<Challenge> challengeList = challengeRepository
                .findAllByStateAndParticipatingDays(ChallengeState.IN_PROGRESS, yesterdayBitPosition);
        if (challengeList.isEmpty()) { return; }
        // 진행 중이면서 위에서 구한 비트 포지션에 부합하는 챌린지 리스트를 조회한다

        LocalDate targetDate = yesterday.toLocalDate();
        List<ParticipationStat> failStatList = new ArrayList<>();
        List<ChallengeParticipationRecord> failRecordList = new ArrayList<>();
        // 이제 레코드를 찾아야 하니까 검사하려는 날의 날짜를 다시 제대로 구한다

        // 챌린지와 레코드 날짜를 기준으로 레코드 리스트를 얻어야 한다
        // 레코드마다 챌린지 포스트 속성이 있는지 검사한다
        // 없을 때만 매번 스탯 불러와서 수정한다 -> 이건 모아서 다시 DB에 저장도 해야 한다
        // 레코드 삭제할 거니까 없는 레코드도 따로 저장해야 한다

//        challengeParticipationRecordRepository.findRecordCheckDTOByChallengeInAndTargetDate(challengeList, targetDate)
//                .forEach(recordDto -> {
//                    if (!recordDto.getRecord().existChallengePost()) {
//                        recordDto.getStat().setFailureCountAndTotalFee(recordDto.getFeePerAbsence());
//                        failStatList.add(recordDto.getStat());
//                        failRecordList.add(recordDto.getRecord());
//                    }
//                });

        participationStatRepository.saveAll(failStatList);
        challengeParticipationRecordRepository.deleteAll(failRecordList);
    }
}
