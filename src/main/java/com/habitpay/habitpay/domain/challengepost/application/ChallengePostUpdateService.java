package com.habitpay.habitpay.domain.challengepost.application;

import com.habitpay.habitpay.domain.challenge.application.ChallengeSearchService;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.application.ChallengeEnrollmentSearchService;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengepost.dao.ChallengePostRepository;
import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.challengepost.dto.ModifyPostRequest;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.postphoto.application.PostPhotoCreationService;
import com.habitpay.habitpay.domain.postphoto.application.PostPhotoDeleteService;
import com.habitpay.habitpay.domain.postphoto.application.PostPhotoUpdateService;
import com.habitpay.habitpay.domain.postphoto.application.PostPhotoUtilService;
import com.habitpay.habitpay.global.error.exception.ErrorCode;
import com.habitpay.habitpay.global.error.exception.ForbiddenException;
import com.habitpay.habitpay.global.response.SuccessCode;
import com.habitpay.habitpay.global.response.SuccessResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengePostUpdateService {

    private final PostPhotoCreationService postPhotoCreationService;
    private final PostPhotoUpdateService postPhotoUpdateService;
    private final PostPhotoUtilService postPhotoUtilService;
    private final PostPhotoDeleteService postPhotoDeleteService;
    private final ChallengeSearchService challengeSearchService;
    private final ChallengePostSearchService challengePostSearchService;
    private final ChallengePostUtilService challengePostUtilService;
    private final ChallengeEnrollmentSearchService challengeEnrollmentSearchService;

    private final ChallengePostRepository challengePostRepository;

    @Transactional
    public SuccessResponse<List<String>> patchPost(ModifyPostRequest request, Long challengeId, Long postId, Member member) {
        Challenge challenge = challengeSearchService.getChallengeById(challengeId);
        ChallengeEnrollment enrollment = challengeEnrollmentSearchService.getByMemberAndChallenge(member, challenge);

        if (enrollment.isGivenUp()) {
            throw new ForbiddenException(ErrorCode.POST_MODIFICATION_FORBIDDEN_DUE_TO_GIVE_UP);
        }

        ChallengePost post = challengePostSearchService.getChallengePostById(postId);


        challengePostUtilService.checkChallengePeriodForPost(post.getChallenge());
        challengePostUtilService.authorizePostWriter(post, member);

        patchContent(post, request.getContent());
        patchIsAnnouncement(post, request.getIsAnnouncement());
        challengePostRepository.save(post);

        postPhotoDeleteService.deleteByIds(postId, request.getDeletedPhotoIds());
        postPhotoUpdateService.changePhotoViewOrder(request.getModifiedPhotos());
        List<String> presignedUrlList = postPhotoCreationService.createPhotoUrlList(
                challengePostSearchService.getChallengePostById(postId), request.getNewPhotos()
        );

        return SuccessResponse.of(
                SuccessCode.PATCH_POST_SUCCESS,
                presignedUrlList
        );
    }

    private void patchContent(ChallengePost post, String content) {
        if (content != null) {
            post.modifyPostContent(content);
        }
    }

    private void patchIsAnnouncement(ChallengePost post, Boolean isAnnouncement) {
        Challenge challenge = challengePostSearchService.getChallengeByPostId(post.getId());
        Member member = post.getWriter();

        if (isAnnouncement != null) {
            if (isAnnouncement && !challengePostUtilService.isChallengeHost(challenge, member)) {
                throw new ForbiddenException(ErrorCode.ONLY_HOST_CAN_UPLOAD_ANNOUNCEMENT);
            }
            post.modifyPostIsAnnouncement(isAnnouncement);
        }
    }

}
