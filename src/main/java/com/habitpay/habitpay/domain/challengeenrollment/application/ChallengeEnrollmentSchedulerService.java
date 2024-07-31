package com.habitpay.habitpay.domain.challengeenrollment.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class ChallengeEnrollmentSchedulerService {

    // todo
    @Scheduled()
    public void checkFailureCount() {

    }
}
