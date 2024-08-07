package com.habitpay.habitpay.domain.challengepost.application;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengepost.dao.ChallengePostRepository;
import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.postphoto.application.PostPhotoDeleteService;
import com.habitpay.habitpay.global.error.exception.ErrorCode;
import com.habitpay.habitpay.global.error.exception.ForbiddenException;
import com.habitpay.habitpay.global.response.SuccessCode;
import com.habitpay.habitpay.global.response.SuccessResponse;
import jakarta.transaction.Transactional;
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
    private final ChallengePostUtilService challengePostUtilService;

    @Transactional
    public SuccessResponse<Void> deletePost(Long postId, Member member) {
        ChallengePost post = challengePostSearchService.getChallengePostById(postId);
        Challenge challenge = post.getChallenge();

        if (post.getIsAnnouncement()) {
            if (!challengePostUtilService.isChallengeHost(challenge, member)) {
                throw new ForbiddenException(ErrorCode.ONLY_HOST_CAN_DELETE_ANNOUNCEMENT);
            }
        }
        else {
            throw new ForbiddenException(ErrorCode.POST_CANNOT_BE_DELETED);
        }

        challengePostUtilService.authorizePostWriter(post, member);

        postPhotoDeleteService.deleteByPost(post);
        challengePostRepository.delete(post);

        return SuccessResponse.of(SuccessCode.DELETE_POST_SUCCESS);
    }

}
