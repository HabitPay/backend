package com.habitpay.habitpay.domain.challengepost.application;

import com.habitpay.habitpay.domain.challenge.application.ChallengeSearchService;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.application.ChallengeEnrollmentSearchService;
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
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
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
    public List<String> save(AddPostRequest request, Long challengeId, String email) {

        Member member = memberService.findByEmail(email);
        Challenge challenge = challengeSearchService.findById(challengeId);
        // todo : List<>로 받게 될 경우 'challenge id' 이용해 enrollment 특정해야 함
        ChallengeEnrollment enrollment = challengeEnrollmentSearchService.findByMember(member)
                .orElseThrow(() -> new NoSuchElementException("(for debugging) no enrollment for : " + email));

        if (request.getIsAnnouncement()) {
            if (!challengePostUtilService.isChallengeHost(challenge, member)) {
                throw new CustomJwtException(HttpStatus.FORBIDDEN, CustomJwtErrorInfo.FORBIDDEN, "Only Host is able to upload an Announcement Post.");
            }
        }

        ChallengePost challengePost = this.savePost(request, enrollment);
        challengePostUtilService.verifyChallengeParticipationRecord(challengePost, enrollment);
        return postPhotoCreationService.save(challengePost, request.getPhotos());
    }

    private ChallengePost savePost(AddPostRequest request, ChallengeEnrollment enrollment) {

        return challengePostRepository.save(request.toEntity(enrollment));
    }

}
