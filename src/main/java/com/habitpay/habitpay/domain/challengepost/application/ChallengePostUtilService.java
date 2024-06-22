package com.habitpay.habitpay.domain.challengepost.application;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.dao.ChallengeEnrollmentRepository;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.refreshtoken.exception.CustomJwtException;
import com.habitpay.habitpay.global.error.CustomJwtErrorInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengePostUtilService {

    private final ChallengeEnrollmentRepository challengeEnrollmentRepository;

    public Member getWriter(ChallengePost post) {
        // todo: enrollment에 findById 메서드로 추후 교체
        ChallengeEnrollment enrollment = challengeEnrollmentRepository.findById(post.getChallengeEnrollmentId())
                .orElseThrow(() -> new NoSuchElementException("No such enrollment " + post.getChallengeEnrollmentId()));
        return enrollment.getMember();
    }

    public void authorizePostWriter(ChallengePost challengePost) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("email : " + email);
        if (!getWriter(challengePost).getEmail().equals(email)) {
            throw new CustomJwtException(HttpStatus.UNAUTHORIZED, CustomJwtErrorInfo.UNAUTHORIZED, "Not a Member who posted.");
        }
    }
    public boolean isChallengeHost(Challenge challenge, Member member) {
        return challenge.getHost().equals(member);
    }

    public boolean isChallengeHost(Challenge challenge, String email) {
        String hostEmail = challenge.getHost().getEmail();
        return hostEmail.equals(email);
    }
}
