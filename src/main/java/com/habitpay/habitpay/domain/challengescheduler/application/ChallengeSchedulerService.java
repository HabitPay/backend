package com.habitpay.habitpay.domain.challengescheduler.application;

import com.habitpay.habitpay.domain.challenge.application.ChallengeSearchService;
import com.habitpay.habitpay.domain.challenge.dao.ChallengeRepository;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challenge.domain.ChallengeState;
import com.habitpay.habitpay.domain.challengeenrollment.dao.ChallengeEnrollmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class ChallengeSchedulerService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeEnrollmentRepository challengeEnrollmentRepository;

    // todo: 챌린지 시작, 종료 시 state 값 변경하는 메서드 (ScheduledExecutorService?)
    //     : 사용자에게 알림 보내는 로직 추가할 수 있음
//    public void startChallenge() {}
//    public void endChallenge() {}

    @Scheduled()
    public void checkParticipationForChallenge() {
        DayOfWeek today = ZonedDateTime.now().getDayOfWeek();
        byte todayBitPosition = (byte) ((byte) 1 << (7 - today.getValue()));
        List<Challenge> challengeList = challengeRepository.findAllByStateAndParticipatingDays(
                ChallengeState.IN_PROGRESS.getBitValue(), todayBitPosition);

        challengeList.stream()
                        .map(challenge -> {
                            challengeEnrollmentRepository.findAllByChallenge(challenge);
                        });
    }

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

    // 기본값은 false
    // 계속 false
    // 포스트 맞게 올리면 true로 바뀜
    // 다음 날도 true인 상태

// --------------------------

// private method : enrollment.failureCount +1
//                  enrollment.totalFee ++
//                  challenge.totalAbsenceFee ++
}
