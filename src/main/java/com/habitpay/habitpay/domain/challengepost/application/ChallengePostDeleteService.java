package com.habitpay.habitpay.domain.challengepost.application;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengepost.dao.ChallengePostRepository;
import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.postphoto.application.PostPhotoDeleteService;
import com.habitpay.habitpay.domain.refreshtoken.exception.CustomJwtException;
import com.habitpay.habitpay.global.error.CustomJwtErrorInfo;
import com.habitpay.habitpay.global.response.SuccessResponse;
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
    public SuccessResponse<Long> deletePost(Long postId, Member member) {
        ChallengePost post = challengePostSearchService.getChallengePostById(postId);
        Challenge challenge = challengePostSearchService.getChallengeByPostId(postId);

        if (post.getIsAnnouncement()) {
            if (!challengePostUtilService.isChallengeHost(challenge, member)) {
                throw new CustomJwtException(HttpStatus.FORBIDDEN, CustomJwtErrorInfo.FORBIDDEN, "공지 포스트는 챌린지 호스트만 삭제할 수 있습니다.");
            }
        }
        else {
            throw new CustomJwtException(HttpStatus.FORBIDDEN, CustomJwtErrorInfo.FORBIDDEN, "일반 포스트 삭제는 제공되지 않는 기능입니다.");
        }

        challengePostUtilService.authorizePostWriter(post, member);

        postPhotoDeleteService.deleteByPost(post);
        challengePostRepository.delete(post);

        return SuccessResponse.of(
                "포스트가 정상적으로 삭제되었습니다.",
                postId
        );
    }

}
