package com.habitpay.habitpay.domain.challengepost.application;

import com.habitpay.habitpay.domain.challenge.application.ChallengeSearchService;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.application.ChallengeEnrollmentSearchService;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengepost.dao.ChallengePostRepository;
import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.challengepost.dto.AddPostRequest;
import com.habitpay.habitpay.domain.member.application.MemberUtilsService;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.postphoto.application.PostPhotoCreationService;
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
public class ChallengePostCreationService {

    private final ChallengeSearchService challengeSearchService;
    private final ChallengePostUtilService challengePostUtilService;
    private final PostPhotoCreationService postPhotoCreationService;
    private final ChallengeEnrollmentSearchService challengeEnrollmentSearchService;
    private final MemberUtilsService memberUtilsService;

    private final ChallengePostRepository challengePostRepository;

    @Transactional
    public SuccessResponse<List<String>> createPost(AddPostRequest request, Long challengeId, Member member) {

        Challenge challenge = challengeSearchService.getChallengeById(challengeId);
        ChallengeEnrollment enrollment = challengeEnrollmentSearchService.getByMemberAndChallenge(member, challenge);

        if (enrollment.isGivenUp()) {
            throw new ForbiddenException(ErrorCode.POST_CREATION_FORBIDDEN_DUE_TO_GIVE_UP);
        }

        challengePostUtilService.checkChallengePeriodForPost(challenge);

        ChallengePost challengePost = this.savePost(request, challenge, member, enrollment);
        if (!challengePost.getIsAnnouncement()) {
            challengePostUtilService.verifyChallengePostForRecord(challengePost);
        }
        List<String> presignedUrlList = postPhotoCreationService.createPhotoUrlList(challengePost, request.getPhotos());

        return SuccessResponse.of(
                SuccessCode.CREATE_POST_SUCCESS,
                presignedUrlList
        );
    }

    private ChallengePost savePost(AddPostRequest request, Challenge challenge, Member member, ChallengeEnrollment enrollment) {

        if (request.getIsAnnouncement()) {
            if (!memberUtilsService.isChallengeHost(challenge, member)) {
                throw new ForbiddenException(ErrorCode.ONLY_HOST_CAN_UPLOAD_ANNOUNCEMENT);
            }
        }

        return challengePostRepository.save(request.toEntity(challenge, enrollment));
    }

}
