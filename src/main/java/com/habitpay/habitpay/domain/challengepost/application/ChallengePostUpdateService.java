package com.habitpay.habitpay.domain.challengepost.application;

import com.habitpay.habitpay.domain.challengeenrollment.dao.ChallengeEnrollmentRepository;
import com.habitpay.habitpay.domain.challengeparticipationrecord.application.ChallengeParticipationRecordService;
import com.habitpay.habitpay.domain.challengepost.dao.ChallengePostRepository;
import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.challengepost.dto.ModifyPostRequest;
import com.habitpay.habitpay.domain.postphoto.application.PostPhotoService;
import com.habitpay.habitpay.domain.refreshtoken.exception.CustomJwtException;
import com.habitpay.habitpay.global.error.CustomJwtErrorInfo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengePostUpdateService {

    private final ChallengePostRepository challengePostRepository;
    private final PostPhotoService postPhotoService;
    private final ChallengePostSearchService challengePostSearchService;
    private final ChallengePostUtilService challengePostUtilService;
    private final ChallengeEnrollmentRepository challengeEnrollmentRepository;
    private final ChallengeParticipationRecordService challengeParticipationRecordService;

    @Transactional
    public ChallengePost update(Long id, ModifyPostRequest request) {
        ChallengePost challengePost = challengePostSearchService.findById(id);

        challengePostUtilService.authorizePostWriter(challengePost);
        if (request.getContent() != null) {
            challengePost.modifyPostContent(request.getContent());
            challengePostRepository.save(challengePost);
        }
        if (request.getIsAnnouncement() != null) {
            if (request.getIsAnnouncement() && !challengePostUtilService.isChallengeHost(challengePostSearchService.findChallengeByPostId(id), challengePostUtilService.getWriter(challengePost))) {
                throw new CustomJwtException(HttpStatus.FORBIDDEN, CustomJwtErrorInfo.FORBIDDEN, "Only Host is able to upload an Announcement Post.");
            }
            challengePost.modifyPostIsAnnouncement(request.getIsAnnouncement());
            challengePostRepository.save(challengePost);
        }

        return challengePost;
    }
}
