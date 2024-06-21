package com.habitpay.habitpay.domain.challengepost.application;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.application.ChallengeEnrollmentSearchService;
import com.habitpay.habitpay.domain.challengeenrollment.dao.ChallengeEnrollmentRepository;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengeparticipationrecord.application.ChallengeParticipationRecordService;
import com.habitpay.habitpay.domain.challengepost.dao.ChallengePostRepository;
import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.challengepost.dto.AddPostRequest;
import com.habitpay.habitpay.domain.challengepost.dto.ModifyPostRequest;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.postphoto.application.PostPhotoService;
import com.habitpay.habitpay.domain.refreshtoken.exception.CustomJwtException;
import com.habitpay.habitpay.global.error.CustomJwtErrorInfo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengePostService {

    private final ChallengePostRepository challengePostRepository;
    private final PostPhotoService postPhotoService;
    private final ChallengeEnrollmentRepository challengeEnrollmentRepository;
    private final ChallengeEnrollmentSearchService challengeEnrollmentSearchService;
    private final ChallengeParticipationRecordService challengeParticipationRecordService;

    public ChallengePost save(AddPostRequest request, Long challengeEnrollmentId) {
        ChallengePost post = challengePostRepository.save(request.toEntity(challengeEnrollmentId));
        // todo : service 메서드로 대체하기
        ChallengeEnrollment enrollment = challengeEnrollmentRepository.findById(challengeEnrollmentId)
                .orElseThrow(() -> new NoSuchElementException("No Such enrollment " + challengeEnrollmentId));
        challengeParticipationRecordService.save(enrollment, post);
        return post;
    }

    // todo : 각 챌린지 별로 findAll 해주는 메서드 (ChallengeEnrollment 도메인 만들고 거기서 ChallengeId 가져온 뒤에 할 수 있을 듯)
    public List<ChallengePost> findAllByChallenge(Long challengeId) {
        return challengePostRepository.findAll();
    }

    // todo : 없는 id를 입력했을 때 예외 던지지 않고 빈 값으로 나와서 뭔가 처리되는 듯. -> 예외 던지기로 고쳐야 함
    public List<ChallengePost> findAllByChallengeEnrollment(Long challengeEnrollmentId) {
        return challengePostRepository.findAllByChallengeEnrollmentId(challengeEnrollmentId)
                .orElseThrow(() -> new NoSuchElementException("(for debugging) not found challengeEnrollmentId : " + challengeEnrollmentId));
    }

    public ChallengePost findById(Long id) {
        return challengePostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("(for debugging) not found : " + id));
    }

    public Challenge findChallengeByPostId(Long postId) {
        ChallengePost post = findById(postId);
        // todo : enrollment service에 findById() 메서드 만들기
        ChallengeEnrollment enrollment = challengeEnrollmentRepository
                .findById(post.getChallengeEnrollmentId())
                .orElseThrow(() -> new NoSuchElementException("No such enrollment " + post.getChallengeEnrollmentId()));
        return enrollment.getChallenge();
    }

    public void delete(Long id) {
        ChallengePost challengePost = findById(id);

        postPhotoService.deleteAllByPost(challengePost);
        challengePostRepository.delete(challengePost);
    }

    @Transactional
    public ChallengePost update(Long id, ModifyPostRequest request) {
        ChallengePost challengePost = findById(id);

        authorizePostWriter(challengePost);
        if (request.getContent() != null) {
            challengePost.modifyPostContent(request.getContent());
            challengePostRepository.save(challengePost);
        }
        if (request.getIsAnnouncement() != null) {
            if (request.getIsAnnouncement() && !isChallengeHost(findChallengeByPostId(id), getWriter(challengePost))) {
                throw new CustomJwtException(HttpStatus.FORBIDDEN, CustomJwtErrorInfo.FORBIDDEN, "Only Host is able to upload an Announcement Post.");
            }
            challengePost.modifyPostIsAnnouncement(request.getIsAnnouncement());
            challengePostRepository.save(challengePost);
        }

        return challengePost;
    }

    public Member getWriter(ChallengePost post) {
        ChallengeEnrollment enrollment = challengeEnrollmentRepository.findById(post.getChallengeEnrollmentId())
                .orElseThrow(() -> new NoSuchElementException("No such enrollment " + post.getChallengeEnrollmentId()));
        return enrollment.getMember();
    }

    public void authorizePostWriter(ChallengePost challengePost) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("email : " + email);
        if (!getWriter(challengePost).getEmail().equals(email)) {
            throw new CustomJwtException(HttpStatus.UNAUTHORIZED, CustomJwtErrorInfo.UNAUTHORIZED, "Not a Member who posted.");
        }
    }
    public boolean isChallengeHost(Challenge challenge, Member member) {
        return challenge.getHost().equals(member);
    }

    public boolean isChallengeHost(Challenge challenge, String email) {
        String hostEmail = challenge.getHost().getEmail();
        return hostEmail.equals(email);
    }
}
