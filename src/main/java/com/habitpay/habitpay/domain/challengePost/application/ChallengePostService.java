package com.habitpay.habitpay.domain.challengePost.application;

import com.habitpay.habitpay.domain.challengePost.dao.ChallengePostRepository;
import com.habitpay.habitpay.domain.challengePost.domain.ChallengePost;
import com.habitpay.habitpay.domain.challengePost.dto.AddPostRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengePostService {

    private final ChallengePostRepository challengePostRepository;

    public ChallengePost save(AddPostRequest request, Long challengeEnrollmentId) {
        return challengePostRepository.save(request.toEntity(challengeEnrollmentId));
    }

    // todo : 각 챌린지 별로 findAll 만들기
//    public List<ChallengePost> findAll() {
//        return challengePostRepository.findAll();
//    }

    public ChallengePost findById(Long id) {
        return challengePostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("(for debugging) not found : " + id));
    }

    // todo : 공지글만 삭제하도록
    public void delete(Long id) {
        ChallengePost challengePost = challengePostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("(for debugging) not found : " + id));

        authorizePostWriter(challengePost);
        challengePostRepository.delete(challengePost);
    }

    // todo : 확인하기
    private static void authorizePostWriter(ChallengePost challengePost) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("email : " + email);
//        if (!challengePost.getWriter().getEmail().equals(email)) {
//            throw new IllegalArgumentException("(for debug) not authorized");
//        }
    }
}
