package com.habitpay.habitpay.domain.challengepost.application;

import com.habitpay.habitpay.domain.challenge.application.ChallengeSearchService;
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
import com.habitpay.habitpay.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengePostSearchService {

    private final PostPhotoSearchService postPhotoSearchService;
    private final PostPhotoUtilService postPhotoUtilService;
    private final ChallengeEnrollmentSearchService challengeEnrollmentSearchService;
    private final ChallengeSearchService challengeSearchService;

    private final ChallengePostRepository challengePostRepository;

    public SuccessResponse<PostViewResponse> getPostViewResponseByPostId(Long postId) {
        ChallengePost challengePost = this.getChallengePostById(postId);
        List<PostPhoto> photoList = postPhotoSearchService.findAllByPost(challengePost);
        List<PostPhotoView> photoViewList = postPhotoUtilService.makePhotoViewList(photoList);

        return SuccessResponse.of(
                "",
                new PostViewResponse(challengePost, photoViewList)
        );
    }

    public SuccessResponse<List<PostViewResponse>> findPostViewResponseListByChallengeId(Long challengeId, Pageable pageable) {

        List<PostViewResponse> postViewResponseList = this.findAllByChallengeId(challengeId, pageable)
                .stream()
                // .filter(post -> !post.getIsAnnouncement()) // todo
                // .sorted() // todo : 순서 설정하고 싶을 때
                .map(post -> {
                    List<PostPhotoView> photoViewList = postPhotoUtilService.makePhotoViewList(postPhotoSearchService.findAllByPost(post));
                    return new PostViewResponse(post, photoViewList);
                })
                .toList();

        return SuccessResponse.of(
                "",
                postViewResponseList
        );
    }

    public SuccessResponse<List<PostViewResponse>> findChallengePostsByMember(Long challengeId, Member member, Pageable pageable) {
        Challenge challenge = challengeSearchService.getChallengeById(challengeId);
        ChallengeEnrollment enrollment = challengeEnrollmentSearchService.findByMemberAndChallenge(member, challenge)
                .orElseThrow(() -> new NoSuchElementException("챌린지에 등록된 멤버가 아닙니다."));

        Long challengeEnrollmentId = enrollment.getId();

        List<PostViewResponse> postViewResponseList =  challengePostRepository.findAllByChallengeEnrollmentId(challengeEnrollmentId, pageable)
                .stream()
                // .filter(post -> !post.getIsAnnouncement()) // todo
                // .sorted() // todo : 순서 설정하고 싶을 때
                .map(post -> {
                    List<PostPhotoView> photoViewList = postPhotoUtilService.makePhotoViewList(postPhotoSearchService.findAllByPost(post));
                    return new PostViewResponse(post, photoViewList);
                })
                .toList();

        return SuccessResponse.of(
                "",
                postViewResponseList
        );
    }

    public ChallengePost getChallengePostById(Long id) {
        return challengePostRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("포스트를 찾을 수 없습니다."));
    }

    public List<ChallengePost> findAllByChallengeId(Long challengeId, Pageable pageable) {
        return challengePostRepository.findAllByChallengeId(challengeId, pageable);
    }

    public Challenge getChallengeByPostId(Long postId) {
        ChallengePost post = getChallengePostById(postId);
        return post.getChallenge();
    }

}
