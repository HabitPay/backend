package com.habitpay.habitpay.domain.challengepost.application;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.application.ChallengeEnrollmentSearchService;
import com.habitpay.habitpay.domain.challengeenrollment.dao.ChallengeEnrollmentRepository;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengepost.dao.ChallengePostRepository;
import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.challengepost.dto.PostViewResponse;
import com.habitpay.habitpay.domain.member.application.MemberService;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.postphoto.application.PostPhotoSearchService;
import com.habitpay.habitpay.domain.postphoto.application.PostPhotoUtilService;
import com.habitpay.habitpay.domain.postphoto.domain.PostPhoto;
import com.habitpay.habitpay.domain.postphoto.dto.PostPhotoView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengePostSearchService {

    private final PostPhotoSearchService postPhotoSearchService;
    private final PostPhotoUtilService postPhotoUtilService;
    private final MemberService memberService;
    private final ChallengeEnrollmentSearchService challengeEnrollmentSearchService;

    private final ChallengePostRepository challengePostRepository;
    private final ChallengeEnrollmentRepository challengeEnrollmentRepository;

    public PostViewResponse findPostById(Long postId) {
        ChallengePost challengePost = this.findById(postId);
        List<PostPhoto> photoList = postPhotoSearchService.findAllByPost(challengePost);
        List<PostPhotoView> photoViewList = postPhotoUtilService.makePhotoViewList(photoList);

        return new PostViewResponse(challengePost, photoViewList);
    }

    public List<PostViewResponse> findChallengePostsByChallengeId(Long challengeId) {

        return this.findAllByChallenge(challengeId)
                .stream()
                .filter(post -> !post.getIsAnnouncement())
                // .sorted() // todo : 순서 설정하고 싶을 때
                .map(post -> new PostViewResponse(post, postPhotoUtilService.makePhotoViewList(postPhotoSearchService.findAllByPost(post))))
                .toList();
    }

    public List<PostViewResponse> findChallengePostsByMe(Long challengeId, String email) {
        Member member = memberService.findByEmail(email);
        // todo : List로 받아서 찾거나 challengeId까지 특정해서 받기
        // ChallengeEnrollment enrollment = challengeEnrollmentSearchService.findByMember(member);

        Long challengeEnrollmentId = 1L; // todo : 임시값

        return this.findAllByChallengeEnrollment(challengeEnrollmentId)
                .stream()
                .filter(post -> !post.getIsAnnouncement())
                // .sorted() // todo : 순서 설정하고 싶을 때
                .map(post -> new PostViewResponse(post, postPhotoUtilService.makePhotoViewList(postPhotoSearchService.findAllByPost(post))))
                .toList();
    }

    // todo : 수정해야 함
    public List<PostViewResponse> findChallengePostsByMember(Long challengeId, String email) {
        Long challengeEnrollmentId = 1L; // todo : 임시값

        return this.findAllByChallengeEnrollment(challengeEnrollmentId)
                .stream()
                .filter(post -> !post.getIsAnnouncement())
                // .sorted() // todo : 순서 설정하고 싶을 때
                .map(post -> new PostViewResponse(post, postPhotoUtilService.makePhotoViewList(postPhotoSearchService.findAllByPost(post))))
                .toList();
    }

    public ChallengePost findById(Long id) {
        return challengePostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("(for debugging) not found : " + id));
    }

    // todo : 각 챌린지 별로 findAll 해주는 메서드 (ChallengeEnrollment 도메인 만들고 거기서 ChallengeId 가져온 뒤에 할 수 있을 듯)
    public List<ChallengePost> findAllByChallenge(Long challengeId) {
        return challengePostRepository.findAll();
    }

    public Challenge findChallengeByPostId(Long postId) {
        ChallengePost post = findById(postId);
        // todo : enrollment service에 findById() 메서드 만들기
        ChallengeEnrollment enrollment = challengeEnrollmentRepository
                .findById(post.getChallengeEnrollmentId())
                .orElseThrow(() -> new NoSuchElementException("No such enrollment " + post.getChallengeEnrollmentId()));
        return enrollment.getChallenge();
    }

    // todo : 없는 id를 입력했을 때 예외 던지지 않고 빈 값으로 나와서 뭔가 처리되는 듯. -> 예외 던지기로 고쳐야 함
    public List<ChallengePost> findAllByChallengeEnrollment(Long challengeEnrollmentId) {
        return challengePostRepository.findAllByChallengeEnrollmentId(challengeEnrollmentId)
                .orElseThrow(() -> new NoSuchElementException("(for debugging) not found challengeEnrollmentId : " + challengeEnrollmentId));
    }

}
