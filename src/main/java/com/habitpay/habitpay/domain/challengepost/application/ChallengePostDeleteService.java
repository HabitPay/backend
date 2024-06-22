package com.habitpay.habitpay.domain.challengepost.application;

import com.habitpay.habitpay.domain.challengepost.dao.ChallengePostRepository;
import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.postphoto.application.PostPhotoDeleteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengePostDeleteService {

    private final ChallengePostRepository challengePostRepository;
    private final PostPhotoDeleteService postPhotoDeleteService;
    private final ChallengePostSearchService challengePostSearchService;

    public void delete(Long id) {
        ChallengePost challengePost = challengePostSearchService.findById(id);

        postPhotoDeleteService.deleteAllByPost(challengePost);
        challengePostRepository.delete(challengePost);
    }
}
