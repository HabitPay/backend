package com.habitpay.habitpay.domain.challengepost.application;

import com.habitpay.habitpay.domain.challenge.application.ChallengeSearchService;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.application.ChallengeEnrollmentSearchService;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengepost.dao.ChallengePostRepository;
import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.challengepost.dto.AddPostRequest;
import com.habitpay.habitpay.domain.member.application.MemberService;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.postphoto.application.PostPhotoCreationService;
import com.habitpay.habitpay.domain.refreshtoken.exception.CustomJwtException;
import com.habitpay.habitpay.global.error.CustomJwtErrorInfo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengePostCreationService {

    private final MemberService memberService;
    private final ChallengeSearchService challengeSearchService;
    private final ChallengePostUtilService challengePostUtilService;
    private final PostPhotoCreationService postPhotoCreationService;
    private final ChallengeEnrollmentSearchService challengeEnrollmentSearchService;

    private final ChallengePostRepository challengePostRepository;

    @Transactional
    public List<String> createPost(AddPostRequest request, Long challengeId, String email) {

        ChallengePost challengePost = this.savePost(request, challengeId, email);
        challengePostUtilService.verifyChallengePostForRecord(challengePost);
        return postPhotoCreationService.createPhotoUrlList(challengePost, request.getPhotos());
    }

    private ChallengePost savePost(AddPostRequest request, Long challengeId, String email) {

        Member member = memberService.findByEmail(email);
        Challenge challenge = challengeSearchService.getChallengeById(challengeId);
        ChallengeEnrollment enrollment = challengeEnrollmentSearchService.findByMemberAndChallenge(member, challenge)
                .orElseThrow(() -> new NoSuchElementException("챌린지에 등록된 멤버가 아니면 포스트를 작성할 수 없습니다."));

        if (request.getIsAnnouncement()) {
            if (!challengePostUtilService.isChallengeHost(challenge, member)) {
                throw new CustomJwtException(HttpStatus.FORBIDDEN, CustomJwtErrorInfo.FORBIDDEN, "공지 포스트는 챌린지 호스트만 작성할 수 있습니다.");
            }
        }

        return challengePostRepository.save(request.toEntity(enrollment));
    }

}
