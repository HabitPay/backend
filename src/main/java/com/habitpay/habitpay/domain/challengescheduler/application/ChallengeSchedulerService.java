package com.habitpay.habitpay.domain.challengescheduler.application;

import com.habitpay.habitpay.domain.challenge.application.ChallengeSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class ChallengeSchedulerService {

    private final ChallengeSearchService challengeSearchService;

    // todo: 챌린지 시작, 종료 시 state 값 변경하는 메서드 (ScheduledExecutorService?)
    //     : 사용자에게 알림 보내는 로직 추가할 수 있음
//    public void startChallenge() {}
//    public void endChallenge() {}

    @Scheduled()
    public void checkParticipationForChallenge() {}

    // 1번 방식

// find all challenge by state=진행 중 and participation days=오늘
// find all enrollment by challenge
// find record by enrollment and 오늘 날짜

// 있으면 패스, 없으면 enrollment.failureCount +1 증가

//--------------------------------

// 2번 방식
// find all challenge by state=진행 중 and participation days=오늘
// find all enrollment by challenge -> isParticipatedToday 속성 추가,,,
//                                     (포스트 등록해서 SuccessCount 증가 시킬 때 true로 바꾸기)
//                                     (그리고 이 메서드 완료 후에 false로 다시 돌려놓기)

// true면 패스, false면 enrollment.failureCount +1 증가

// --------------------------

// private method : enrollment.failureCount +1
//                  enrollment.totalFee ++
//                  challenge.totalAbsenceFee ++
}
