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
import com.habitpay.habitpay.global.response.SuccessResponse;
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
    public SuccessResponse<List<String>> patchPost(ModifyPostRequest request, Long postId, Member member) {
        ChallengePost post = challengePostSearchService.getChallengePostById(postId);
        challengePostUtilService.authorizePostWriter(post, member);

        patchContent(post, request.getContent());
        patchIsAnnouncement(post, request.getIsAnnouncement());

        postPhotoDeleteService.deleteByIds(postId, request.getDeletedPhotoIds());
        request.getModifiedPhotos().forEach(photo -> postPhotoUtilService.changeViewOrder(photo.getPhotoId(), photo.getViewOrder()));

        List<String> presignedUrlList = postPhotoCreationService.createPhotoUrlList(
                challengePostSearchService.getChallengePostById(postId), request.getNewPhotos()
        );

        return SuccessResponse.of(
                "포스트가 수정되었습니다.",
                presignedUrlList
        );
    }

    private void patchContent(ChallengePost post, String content) {
        if (content != null) {
            post.modifyPostContent(content);
            challengePostRepository.save(post);
        }
    }

    private void patchIsAnnouncement(ChallengePost post, Boolean isAnnouncement) {
        Challenge challenge = challengePostSearchService.getChallengeByPostId(post.getId());
        Member member = post.getWriter();

        if (isAnnouncement != null) {
            if (isAnnouncement && !challengePostUtilService.isChallengeHost(challenge, member)) {
                throw new CustomJwtException(HttpStatus.FORBIDDEN, CustomJwtErrorInfo.FORBIDDEN, "공지 포스트는 챌린지 호스트만 작성할 수 있습니다.");
            }
            post.modifyPostIsAnnouncement(isAnnouncement);
            challengePostRepository.save(post);
        }
    }

}
