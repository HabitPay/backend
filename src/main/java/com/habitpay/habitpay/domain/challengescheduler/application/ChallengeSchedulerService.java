package com.habitpay.habitpay.domain.challengescheduler.application;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class ChallengeSchedulerService {

    // todo : Challenge 시작, 종료 시 state 변경 등

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void checkParticipationForChallenge() {
        DayOfWeek yesterdayOfWeek = ZonedDateTime.now().minusDays(1).getDayOfWeek();
        byte yesterdayBitPosition = (byte) ((byte) 1 << (7 - yesterdayOfWeek.getValue()));

//        1. Challenge 목록 DB 조회(진행 중, 요일 기준)
//        2. 1번의 목록을 기준으로 record 목록 DB 조회 (어제 날짜 기준)
//        3. 2번의 목록을 순회하며 challengePost 존재 여부에 따라,
//                record에 getParticipationStat()을 이용해 setter로 값 변경
//        4. 값이 변경된 stat을 따로 새 list로 모아뒀다가 DB 업데이트
//                (or @Transactional 어노테이션을 이용해 자동으로 DB에 반영되게끔 하기)
    }
}
