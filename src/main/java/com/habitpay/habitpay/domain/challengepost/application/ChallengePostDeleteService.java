package com.habitpay.habitpay.domain.challengepost.application;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengepost.dao.ChallengePostRepository;
import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.postphoto.application.PostPhotoDeleteService;
import com.habitpay.habitpay.domain.refreshtoken.exception.CustomJwtException;
import com.habitpay.habitpay.global.error.CustomJwtErrorInfo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengePostDeleteService {

    private final ChallengePostRepository challengePostRepository;
    private final PostPhotoDeleteService postPhotoDeleteService;
    private final ChallengePostSearchService challengePostSearchService;
    private final ChallengePostUtilService challengePostUtilService;

    @Transactional
    public void delete(Long postId, String email) {
        ChallengePost post = challengePostSearchService.findById(postId);
        Challenge challenge = challengePostSearchService.findChallengeByPostId(postId);

        if (post.getIsAnnouncement()) {
            if (!challengePostUtilService.isChallengeHost(challenge, email)) {
                throw new CustomJwtException(HttpStatus.FORBIDDEN, CustomJwtErrorInfo.FORBIDDEN, "Only Host is able to delete an Announcement Post.");
            }
        }
        else {
            throw new CustomJwtException(HttpStatus.FORBIDDEN, CustomJwtErrorInfo.FORBIDDEN, "Post cannot be deleted.");
        }

        this.deletePost(postId);
    }

    private void deletePost(Long id) {
        ChallengePost challengePost = challengePostSearchService.findById(id);

        postPhotoDeleteService.deleteAllByPost(challengePost);
        challengePostRepository.delete(challengePost);
    }
}
