package com.habitpay.habitpay.domain.challengepost.application;

import com.habitpay.habitpay.domain.challengeenrollment.dao.ChallengeEnrollmentRepository;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengeparticipationrecord.application.ChallengeParticipationRecordCreationService;
import com.habitpay.habitpay.domain.challengepost.dao.ChallengePostRepository;
import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.challengepost.dto.AddPostRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengePostCreationService {

    private final ChallengePostRepository challengePostRepository;
    private final ChallengeEnrollmentRepository challengeEnrollmentRepository;
    private final ChallengeParticipationRecordCreationService challengeParticipationRecordCreationService;

    public ChallengePost save(AddPostRequest request, Long challengeEnrollmentId) {
        ChallengePost post = challengePostRepository.save(request.toEntity(challengeEnrollmentId));
        // todo : service 메서드로 대체하기
        ChallengeEnrollment enrollment = challengeEnrollmentRepository.findById(challengeEnrollmentId)
                .orElseThrow(() -> new NoSuchElementException("No Such enrollment " + challengeEnrollmentId));
        challengeParticipationRecordCreationService.save(enrollment, post);
        return post;
    }

}
