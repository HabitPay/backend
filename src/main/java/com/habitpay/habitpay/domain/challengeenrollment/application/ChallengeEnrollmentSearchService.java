package com.habitpay.habitpay.domain.challengeenrollment.application;

import com.habitpay.habitpay.domain.challengeenrollment.dao.ChallengeEnrollmentRepository;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChallengeEnrollmentSearchService {
    private final ChallengeEnrollmentRepository challengeEnrollmentRepository;

    public Optional<ChallengeEnrollment> findByMember(Member member) {
        return challengeEnrollmentRepository.findByMember(member);
    }
}
