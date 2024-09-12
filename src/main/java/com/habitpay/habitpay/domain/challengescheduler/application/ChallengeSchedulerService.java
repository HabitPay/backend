package com.habitpay.habitpay.domain.challengescheduler.application;

import com.habitpay.habitpay.domain.challenge.dao.ChallengeRepository;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challenge.domain.ChallengeState;
import com.habitpay.habitpay.domain.challengeenrollment.dao.ChallengeEnrollmentRepository;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengeparticipationrecord.application.ChallengeParticipationRecordSearchService;
import com.habitpay.habitpay.domain.challengeparticipationrecord.dao.ChallengeParticipationRecordRepository;
import com.habitpay.habitpay.domain.challengeparticipationrecord.domain.ChallengeParticipationRecord;
import com.habitpay.habitpay.domain.member.domain.Member;
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
    private final ChallengeEnrollmentRepository challengeEnrollmentRepository;
    private final ChallengeParticipationRecordSearchService challengeParticipationRecordSearchService;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void setChallengeForStart() {
//        // 오늘 날짜 시작인 챌린지 리스트 얻기
//        // todo : 시작 날짜 ZonedDateTime으로 받는 거 데이터 타입 확인하기
////        List<Challenge> challengeList = ;
//        List<Challenge> challengeList = new ArrayList<>();
//
//        // 챌린지 리스트를 state 진행 중으로 바꾸기
//        challengeList = challengeList
//                .stream()
//                .map(Challenge::setStateInProgress)
//                .toList();
//
//        // 각 챌린지 내의 멤버(등록) 리스트 얻기
//        List<ChallengeEnrollment> enrollmentList = challengeEnrollmentRepository
//                .findAllByChallengeIn(challengeList);
//
//        // todo : 멤버 리스트 순회하면서 참여 목록 한 번에 모두! 만들기
//        // 등록 객체 -> 챌린지 통해 필요한 날짜 구하기 -> 날짜 넣어서 record 만들기(외래키 연결과 함께)
//        // 모든 등록 객체를 이렇게 순회? 그러면 챌린지 필요 날짜 구하기 작업이 불필요하게 반복되는 듯함

        // -------------------------------------

        // 오늘 날짜 시작 && scheduled 챌린지 리스트 얻기
        // todo : 시작 날짜 ZonedDateTime으로 받는 거 데이터 타입 확인하기
//        List<Challenge> challengeList = ;
        List<Challenge> challengeList = new ArrayList<>();

        challengeList = challengeList
                .stream()
                .map(Challenge::setStateInProgress)
                .toList();

        // 챌린지 별로 순회
        challengeList.forEach(challenge -> {
            // 시작 날짜부터 종료 날짜까지 && 참여 요일
        });
        // 챌린지 참여 날짜 구하기
        // 챌린지 참여 날짜 순회 -> 챌린지에 해당하는 등록 리스트를 각각 순회시켜서 recrod 생성

        // 챌린지 상태가 변경되었으므로 챌린지를 레포에 저장하기
        // 새로 생성된 record 리스트들 저장하기

        // -------------------------------------
        // 챌린지 생성일이 시작 요일과 동일한 경우만 참여 목록 따로 생성하는 예외 처리 필요
        //  => 당일 시작 챌린지는 반드시 처음부터 상태를 진행 중으로 만들어야 함!
        // 00시부터 시작하는 챌린지 -> 당일 시작하는 챌린지를 생성할 수 없다면 사용자 경험은? (다음 날부터 가능)
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
