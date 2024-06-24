package com.habitpay.habitpay.domain.challengepost.application;

import com.habitpay.habitpay.domain.challengepost.dao.ChallengePostRepository;
import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.postphoto.application.PostPhotoDeleteService;
import com.habitpay.habitpay.domain.refreshtoken.exception.CustomJwtException;
import com.habitpay.habitpay.global.error.CustomJwtErrorInfo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public void deletePost(Long postId, String email) {
        ChallengePost post = challengePostSearchService.findById(postId);

        if (post.getIsAnnouncement()) {
            if (!challengePostUtilService.isChallengeHost(challengePostSearchService.findChallengeByPostId(postId), email)) {
                throw new CustomJwtException(HttpStatus.FORBIDDEN, CustomJwtErrorInfo.FORBIDDEN, "Only Host is able to delete an Announcement Post.");
            }
        }
        else {
            // todo : 관리자 계정이 생겨서 일반 포스트도 삭제할 수 있게 되면 수정
            throw new CustomJwtException(HttpStatus.FORBIDDEN, CustomJwtErrorInfo.FORBIDDEN, "Post cannot be deleted.");
        }

        this.delete(postId);
    }

    private void delete(Long id) {
        ChallengePost challengePost = challengePostSearchService.findById(id);

        postPhotoDeleteService.deleteAllByPost(challengePost);
        challengePostRepository.delete(challengePost);
    }
}
