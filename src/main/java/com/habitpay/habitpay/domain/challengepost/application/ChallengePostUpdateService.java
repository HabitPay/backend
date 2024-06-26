package com.habitpay.habitpay.domain.challengepost.application;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengepost.dao.ChallengePostRepository;
import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.challengepost.dto.ModifyPostRequest;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.postphoto.application.PostPhotoCreationService;
import com.habitpay.habitpay.domain.postphoto.application.PostPhotoDeleteService;
import com.habitpay.habitpay.domain.postphoto.application.PostPhotoUtilService;
import com.habitpay.habitpay.domain.refreshtoken.exception.CustomJwtException;
import com.habitpay.habitpay.global.error.CustomJwtErrorInfo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengePostUpdateService {

    private final PostPhotoCreationService postPhotoCreationService;
    private final PostPhotoUtilService postPhotoUtilService;
    private final PostPhotoDeleteService postPhotoDeleteService;
    private final ChallengePostSearchService challengePostSearchService;
    private final ChallengePostUtilService challengePostUtilService;

    private final ChallengePostRepository challengePostRepository;

    @Transactional
    public List<String> update(ModifyPostRequest request, Long postId) {
        this.updatePost(request, postId);

        postPhotoDeleteService.deleteByIds(postId, request.getDeletedPhotoIds());
        request.getModifiedPhotos().forEach(photo -> postPhotoUtilService.changeViewOrder(photo.getPhotoId(), photo.getViewOrder()));
        return postPhotoCreationService.save(challengePostSearchService.findById(postId), request.getNewPhotos());
    }

    private void updatePost(ModifyPostRequest request, Long id) {
        ChallengePost challengePost = challengePostSearchService.findById(id);
        challengePostUtilService.authorizePostWriter(challengePost);

        updateContent(challengePost, request.getContent());
        updateIsAnnouncement(challengePost, request.getIsAnnouncement());
    }

    private void updateContent(ChallengePost post, String content) {
        if (content != null) {
            post.modifyPostContent(content);
            challengePostRepository.save(post);
        }
    }

    private void updateIsAnnouncement(ChallengePost post, Boolean isAnnouncement) {
        Challenge challenge = challengePostSearchService.findChallengeByPostId(post.getId());
        Member member = post.getWriter();

        if (isAnnouncement != null) {
            if (isAnnouncement && !challengePostUtilService.isChallengeHost(challenge, member)) {
                throw new CustomJwtException(HttpStatus.FORBIDDEN, CustomJwtErrorInfo.FORBIDDEN, "Only Host is able to upload an Announcement Post.");
            }
            post.modifyPostIsAnnouncement(isAnnouncement);
            challengePostRepository.save(post);
        }
    }

}
