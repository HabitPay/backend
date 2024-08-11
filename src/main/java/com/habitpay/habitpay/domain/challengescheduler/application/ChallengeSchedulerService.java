package com.habitpay.habitpay.domain.challengescheduler.application;

import com.habitpay.habitpay.domain.challenge.dao.ChallengeRepository;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challenge.domain.ChallengeState;
import com.habitpay.habitpay.domain.challengeenrollment.dao.ChallengeEnrollmentRepository;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
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

    // todo: 챌린지 시작, 종료 시 state 값 변경하는 메서드
    //     : 사용자에게 알림 보내는 로직 추가할 수 있음
//    public void startChallenge() {}
//    public void endChallenge() {}

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void checkParticipationForChallenge() {
        DayOfWeek yesterdayOfWeek = ZonedDateTime.now().minusDays(1).getDayOfWeek();
        byte todayBitPosition = (byte) ((byte) 1 << (7 - yesterdayOfWeek.getValue()));

        List<Challenge> challengeList = challengeRepository.findAllByStateAndParticipatingDays(
                ChallengeState.IN_PROGRESS.getBitValue(), todayBitPosition);

            challengeEnrollmentRepository.findAllByChallengeIn(challengeList)
                    .forEach(enrollment -> {
                                if (!enrollment.isParticipatedToday()) {
                            enrollment.plusFailureCountWithScheduler();
                        }
                                enrollment.resetIsParticipatedToday();
                    });
    }
}
