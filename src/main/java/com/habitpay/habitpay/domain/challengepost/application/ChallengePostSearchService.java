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
import com.habitpay.habitpay.global.config.aws.S3FileService;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengePostSearchService {

    private final PostPhotoSearchService postPhotoSearchService;
    private final PostPhotoUtilService postPhotoUtilService;
    private final ChallengeEnrollmentSearchService challengeEnrollmentSearchService;
    private final ChallengeSearchService challengeSearchService;
    private final S3FileService s3FileService;

    private final ChallengePostRepository challengePostRepository;

    public SuccessResponse<PostViewResponse> getPostViewByPostId(Long postId) {
        ChallengePost challengePost = this.getChallengePostById(postId);
        List<PostPhoto> photoList = postPhotoSearchService.findAllByPost(challengePost);
        List<PostPhotoView> photoViewList = postPhotoUtilService.makePhotoViewList(photoList);

        // todo: 반복해서 사용되므로 메서드화하기 (member 서비스 내 코드가 원조)
        Member writer = challengePost.getWriter();
        String imageFileName = Optional.ofNullable(writer.getImageFileName()).orElse("");
        String imageUrl = imageFileName.isEmpty() ? "" : s3FileService.getGetPreSignedUrl("profiles", imageFileName);

        return SuccessResponse.of(
                SuccessCode.NO_MESSAGE,
                new PostViewResponse(challengePost, imageUrl, photoViewList)
        );
    }

    public SuccessResponse<Slice<PostViewResponse>> findPostViewListByChallengeId(Long challengeId, Pageable pageable) {

        Slice<ChallengePost> postSlice = challengePostRepository.findAllByChallengeId(challengeId, pageable);
        List<PostViewResponse> postViewResponseList = makePostViewResponseList(postSlice);
        Slice<PostViewResponse> postViewResponseSlice = new SliceImpl<>(
                postViewResponseList, postSlice.getPageable(), postSlice.hasNext()
        );

        return SuccessResponse.of(
                SuccessCode.NO_MESSAGE,
                postViewResponseSlice
        );
    }

    public SuccessResponse<Slice<PostViewResponse>> findAnnouncementPostViewListByChallengeId(Long challengeId, Pageable pageable) {

        Slice<ChallengePost> postSlice = challengePostRepository.findAllByChallengeIdAndIsAnnouncementTrue(challengeId, pageable);
        List<PostViewResponse> postViewResponseList = makePostViewResponseList(postSlice);
        Slice<PostViewResponse> postViewResponseSlice = new SliceImpl<>(
                postViewResponseList, postSlice.getPageable(), postSlice.hasNext()
        );

        return SuccessResponse.of(
                SuccessCode.NO_MESSAGE,
                postViewResponseSlice
        );
    }

    public SuccessResponse<Slice<PostViewResponse>> findPostViewListByMember(Long challengeId, Member member, Pageable pageable) {
        Challenge challenge = challengeSearchService.getChallengeById(challengeId);
        ChallengeEnrollment enrollment = challengeEnrollmentSearchService.findByMemberAndChallenge(member, challenge)
                .orElseThrow(() -> new NotEnrolledChallengeException(member.getId(), challengeId));

        Slice<ChallengePost> postSlice = challengePostRepository.findAllByChallengeEnrollment(enrollment, pageable);
        List<PostViewResponse> postViewResponseList =  makePostViewResponseList(postSlice);
        Slice<PostViewResponse> postViewResponseSlice = new SliceImpl<>(
                postViewResponseList, postSlice.getPageable(), postSlice.hasNext()
        );

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
                    // todo: 반복 코드
                    Member writer = post.getWriter();
                    String imageFileName = Optional.ofNullable(writer.getImageFileName()).orElse("");
                    String imageUrl = imageFileName.isEmpty() ? "" : s3FileService.getGetPreSignedUrl("profiles", imageFileName);
                    return new PostViewResponse(post, imageUrl, photoViewList);
                })
                .toList();
    }

}
