package com.habitpay.habitpay.domain.challengepost.application;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.dao.ChallengeEnrollmentRepository;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengepost.dao.ChallengePostRepository;
import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengePostSearchService {

    private final ChallengePostRepository challengePostRepository;
    private final ChallengeEnrollmentRepository challengeEnrollmentRepository;


    public ChallengePost findById(Long id) {
        return challengePostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("(for debugging) not found : " + id));
    }

    // todo : 각 챌린지 별로 findAll 해주는 메서드 (ChallengeEnrollment 도메인 만들고 거기서 ChallengeId 가져온 뒤에 할 수 있을 듯)
    public List<ChallengePost> findAllByChallenge(Long challengeId) {
        return challengePostRepository.findAll();
    }

    public Challenge findChallengeByPostId(Long postId) {
        ChallengePost post = findById(postId);
        // todo : enrollment service에 findById() 메서드 만들기
        ChallengeEnrollment enrollment = challengeEnrollmentRepository
                .findById(post.getChallengeEnrollmentId())
                .orElseThrow(() -> new NoSuchElementException("No such enrollment " + post.getChallengeEnrollmentId()));
        return enrollment.getChallenge();
    }

    // todo : 없는 id를 입력했을 때 예외 던지지 않고 빈 값으로 나와서 뭔가 처리되는 듯. -> 예외 던지기로 고쳐야 함
    public List<ChallengePost> findAllByChallengeEnrollment(Long challengeEnrollmentId) {
        return challengePostRepository.findAllByChallengeEnrollmentId(challengeEnrollmentId)
                .orElseThrow(() -> new NoSuchElementException("(for debugging) not found challengeEnrollmentId : " + challengeEnrollmentId));
    }

}
