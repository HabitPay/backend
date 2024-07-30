package com.habitpay.habitpay.domain.challengepost.application;

import com.habitpay.habitpay.domain.challenge.application.ChallengeSearchService;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.application.ChallengeEnrollmentSearchService;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengeenrollment.exception.NotEnrolledChallengeException;
import com.habitpay.habitpay.domain.challengepost.dao.ChallengePostRepository;
import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.challengepost.dto.PostViewResponse;
import com.habitpay.habitpay.domain.challengepost.exception.PostNotFoundException;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.postphoto.application.PostPhotoSearchService;
import com.habitpay.habitpay.domain.postphoto.application.PostPhotoUtilService;
import com.habitpay.habitpay.domain.postphoto.domain.PostPhoto;
import com.habitpay.habitpay.domain.postphoto.dto.PostPhotoView;
import com.habitpay.habitpay.global.response.SuccessCode;
import com.habitpay.habitpay.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
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

    public SuccessResponse<PostViewResponse> getPostViewByPostId(Long postId) {
        ChallengePost challengePost = this.getChallengePostById(postId);
        List<PostPhoto> photoList = postPhotoSearchService.findAllByPost(challengePost);
        List<PostPhotoView> photoViewList = postPhotoUtilService.makePhotoViewList(photoList);

        return SuccessResponse.of(
                SuccessCode.NO_MESSAGE,
                new PostViewResponse(challengePost, photoViewList)
        );
    }

    public SuccessResponse<Slice<PostViewResponse>> findPostViewListByChallengeId(Long challengeId, Pageable pageable) {

        Slice<ChallengePost> postSlice = challengePostRepository.findAllByChallengeId(challengeId, pageable);

        List<PostViewResponse> postViewResponseList = postSlice.stream()
                .map(post -> {
                    List<PostPhotoView> photoViewList = postPhotoUtilService.makePhotoViewList(postPhotoSearchService.findAllByPost(post));
                    return new PostViewResponse(post, photoViewList);
                })
                .toList();

        Slice<PostViewResponse> postViewResponseSlice = new SliceImpl<>(postViewResponseList, postSlice.getPageable(), postSlice.hasNext());

        return SuccessResponse.of(
                SuccessCode.NO_MESSAGE,
                postViewResponseSlice
        );
    }

    public SuccessResponse<Slice<PostViewResponse>> findAnnouncementPostViewListByChallengeId(Long challengeId, Pageable pageable) {

        Slice<ChallengePost> postSlice = challengePostRepository.findAllByChallengeIdAndIsAnnouncementTrue(challengeId, pageable);

        List<PostViewResponse> postViewResponseList = postSlice.stream()
                .map(post -> {
                    List<PostPhotoView> photoViewList = postPhotoUtilService.makePhotoViewList(postPhotoSearchService.findAllByPost(post));
                    return new PostViewResponse(post, photoViewList);
                })
                .toList();

        Slice<PostViewResponse> postViewResponseSlice = new SliceImpl<>(postViewResponseList, postSlice.getPageable(), postSlice.hasNext());

        return SuccessResponse.of(
                SuccessCode.NO_MESSAGE,
                postViewResponseSlice
        );
    }

    public SuccessResponse<Slice<PostViewResponse>> findPostViewListByMember(Long challengeId, Member member, Pageable pageable) {
        Challenge challenge = challengeSearchService.getChallengeById(challengeId);
        ChallengeEnrollment enrollment = challengeEnrollmentSearchService.findByMemberAndChallenge(member, challenge)
                .orElseThrow(() -> new NotEnrolledChallengeException(member.getId(), challengeId));

        Long challengeEnrollmentId = enrollment.getId();

        Slice<ChallengePost> postSlice = challengePostRepository.findAllByChallengeEnrollmentId(challengeEnrollmentId, pageable);

        List<PostViewResponse> postViewResponseList =  postSlice.stream()
                .map(post -> {
                    List<PostPhotoView> photoViewList = postPhotoUtilService.makePhotoViewList(postPhotoSearchService.findAllByPost(post));
                    return new PostViewResponse(post, photoViewList);
                })
                .toList();

        Slice<PostViewResponse> postViewResponseSlice = new SliceImpl<>(postViewResponseList, postSlice.getPageable(), postSlice.hasNext());

        return SuccessResponse.of(
                SuccessCode.NO_MESSAGE,
                postViewResponseSlice
        );
    }

    public ChallengePost getChallengePostById(Long id) {
        return challengePostRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
    }

    public Challenge getChallengeByPostId(Long postId) {
        ChallengePost post = getChallengePostById(postId);
        return post.getChallenge();
    }

    private List<PostViewResponse> makePostViewResponseList(Slice<ChallengePost> postList) {
        return postList.stream()
                .map(post -> {
                    List<PostPhotoView> photoViewList = postPhotoUtilService.makePhotoViewList(postPhotoSearchService.findAllByPost(post));
                    return new PostViewResponse(post, photoViewList);
                })
                .toList();
    }

}
