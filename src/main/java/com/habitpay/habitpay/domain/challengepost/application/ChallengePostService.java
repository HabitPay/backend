package com.habitpay.habitpay.domain.challengepost.application;

import com.habitpay.habitpay.domain.challengepost.dao.ChallengePostRepository;
import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.challengepost.dto.AddPostRequest;
import com.habitpay.habitpay.domain.challengepost.dto.ModifyPostRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengePostService {

    private final ChallengePostRepository challengePostRepository;

    public ChallengePost save(AddPostRequest request, Long challengeEnrollmentId) {
        return challengePostRepository.save(request.toEntity(challengeEnrollmentId));
    }

    // todo : 각 챌린지 별로 findAll 해주는 메서드 (ChallengeEnrollment 도메인 만들고 거기서 ChallengeId 가져온 뒤에 할 수 있을 듯)
    public List<ChallengePost> findAllByChallenge(Long challengeId) {
        return challengePostRepository.findAll();
    }

    // todo : 없는 id를 입력했을 때 예외 던지지 않고 빈 값으로 나와서 뭔가 처리되는 듯. -> 예외 던지기로 고쳐야 함
    public List<ChallengePost> findAllByChallengeEnrollment(Long challengeEnrollmentId) {
        return challengePostRepository.findAllByChallengeEnrollmentId(challengeEnrollmentId)
                .orElseThrow(() -> new NoSuchElementException("(for debugging) not found challengeEnrollmentId : " + challengeEnrollmentId));
    }

    public ChallengePost findById(Long id) {
        return challengePostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("(for debugging) not found : " + id));
    }

    // todo : 공지글만 삭제 가능하도록
    public void delete(Long id) {
        ChallengePost challengePost = challengePostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("(for debugging) not found : " + id));

        authorizePostWriter(challengePost); // todo : method 미완
        challengePostRepository.delete(challengePost);
    }

    @Transactional
    public ChallengePost update(Long id, ModifyPostRequest request) {
        ChallengePost challengePost = challengePostRepository.findById(id)
                .orElseThrow(() ->  new IllegalArgumentException("(for debugging) not found : " + id));

        authorizePostWriter(challengePost);
        if (request.getContent() != null) {
            challengePost.modifyPostContent(request.getContent());
        }
        if (request.getIsAnnouncement() != null) {
            challengePost.modifyPostIsAnnouncement(request.getIsAnnouncement());
        }

        return challengePost;
    }

    // todo : 확인하기
    private static void authorizePostWriter(ChallengePost challengePost) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("email : " + email);
//        if (!challengePost.getWriter().getEmail().equals(email)) {
//            throw new IllegalArgumentException("(for debug) not authorized");
//        }
    }
}
