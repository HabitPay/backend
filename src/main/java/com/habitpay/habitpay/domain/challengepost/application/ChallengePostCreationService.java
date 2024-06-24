package com.habitpay.habitpay.domain.challengepost.application;

import com.habitpay.habitpay.domain.challenge.application.ChallengeSearchService;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.application.ChallengeEnrollmentSearchService;
import com.habitpay.habitpay.domain.challengeenrollment.dao.ChallengeEnrollmentRepository;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengeparticipationrecord.application.ChallengeParticipationRecordCreationService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

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
    private final ChallengeParticipationRecordCreationService challengeParticipationRecordCreationService;

    private final ChallengePostRepository challengePostRepository;
    private final ChallengeEnrollmentRepository challengeEnrollmentRepository;

    @Transactional
    public List<String> addPost(AddPostRequest request, Long challengeId, String email) {

        Member member = memberService.findByEmail(email);
        // todo : List<>로 받게 될 경우 'challenge id' 이용해 enrollment 특정해야 함
        ChallengeEnrollment enrollment = challengeEnrollmentSearchService.findByMember(member)
                .orElseThrow(() -> new NoSuchElementException("(for debugging) no enrollment for : " + email));

        if (request.getIsAnnouncement()) {
            Challenge challenge = challengeSearchService.findById(challengeId);
            // todo : isChallengeHost가 email, member 모두 사용 가능
            if (!challengePostUtilService.isChallengeHost(challenge, member)) {
                throw new CustomJwtException(HttpStatus.FORBIDDEN, CustomJwtErrorInfo.FORBIDDEN, "Only Host is able to upload an Announcement Post.");
            }
        }

        ChallengePost challengePost = this.save(request, enrollment.getId());
        return postPhotoCreationService.save(challengePost, request.getPhotos());
    }

    private ChallengePost save(AddPostRequest request, Long challengeEnrollmentId) {
        ChallengePost post = challengePostRepository.save(request.toEntity(challengeEnrollmentId));
        // todo : service 메서드로 대체하기
        ChallengeEnrollment enrollment = challengeEnrollmentRepository.findById(challengeEnrollmentId)
                .orElseThrow(() -> new NoSuchElementException("No Such enrollment " + challengeEnrollmentId));
        challengeParticipationRecordCreationService.save(enrollment, post);
        return post;
    }

}
