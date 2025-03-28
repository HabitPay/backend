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
import com.habitpay.habitpay.global.response.SliceResponse;
import com.habitpay.habitpay.global.response.SuccessCode;
import com.habitpay.habitpay.global.response.SuccessResponse;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;

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

    public SuccessResponse<PostViewResponse> getPostViewByPostId(Long postId, Member member) {
        ChallengePost challengePost = this.getChallengePostById(postId);
        List<PostPhoto> photoList = postPhotoSearchService.findAllByPost(challengePost);
        List<PostPhotoView> photoViewList = postPhotoUtilService.makePhotoViewList(photoList);

        Member writer = challengePost.getWriter();
        Boolean isPostAuthor = member.equals(writer);

        String imageFileName = Optional.ofNullable(writer.getImageFileName()).orElse("");
        String imageUrl = imageFileName.isEmpty() ? ""
            : s3FileService.getGetPreSignedUrl("profiles", imageFileName);

        return SuccessResponse.of(
            SuccessCode.NO_MESSAGE,
            new PostViewResponse(challengePost, isPostAuthor, imageUrl, photoViewList)
        );
    }

    public SuccessResponse<SliceResponse<PostViewResponse>> findPostViewListByChallengeId(
        Long challengeId,
        Member member, Pageable pageable) {

        Slice<ChallengePost> postSlice = challengePostRepository.findAllByChallengeId(challengeId,
            pageable);
        List<PostViewResponse> postViewResponseList = makePostViewResponseList(postSlice, member);
        Slice<PostViewResponse> postViewResponseSlice = new SliceImpl<>(
            postViewResponseList, postSlice.getPageable(), postSlice.hasNext()
        );

        return SuccessResponse.of(
            SuccessCode.NO_MESSAGE,
            SliceResponse.from(postViewResponseSlice)
        );
    }

    public SuccessResponse<SliceResponse<PostViewResponse>> findAnnouncementPostViewListByChallengeId(
        Long challengeId, Member member, Pageable pageable) {

        Slice<ChallengePost> postSlice = challengePostRepository.findAllByChallengeIdAndIsAnnouncementTrue(
            challengeId, pageable);
        List<PostViewResponse> postViewResponseList = makePostViewResponseList(postSlice, member);
        Slice<PostViewResponse> postViewResponseSlice = new SliceImpl<>(
            postViewResponseList, postSlice.getPageable(), postSlice.hasNext()
        );

        return SuccessResponse.of(
            SuccessCode.NO_MESSAGE,
                SliceResponse.from(postViewResponseSlice)
        );
    }

    public SuccessResponse<SliceResponse<PostViewResponse>> findPostViewListByMember(Long challengeId,
        Member member, Pageable pageable) {
        Challenge challenge = challengeSearchService.getChallengeById(challengeId);
        ChallengeEnrollment enrollment = challengeEnrollmentSearchService.findByMemberAndChallenge(
                member, challenge)
            .orElseThrow(() -> new NotEnrolledChallengeException(member.getId(), challengeId));

        Slice<ChallengePost> postSlice = challengePostRepository.findAllByChallengeEnrollment(
            enrollment, pageable);
        List<PostViewResponse> postViewResponseList = makePostViewResponseList(postSlice, member);
        Slice<PostViewResponse> postViewResponseSlice = new SliceImpl<>(
            postViewResponseList, postSlice.getPageable(), postSlice.hasNext()
        );

        return SuccessResponse.of(
            SuccessCode.NO_MESSAGE,
                SliceResponse.from(postViewResponseSlice)
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

    private List<PostViewResponse> makePostViewResponseList(Slice<ChallengePost> postList,
        Member member) {
        return postList.stream()
            .map(post -> {
                List<PostPhotoView> photoViewList = postPhotoUtilService.makePhotoViewList(
                    postPhotoSearchService.findAllByPost(post));
                Member writer = post.getWriter();
                Boolean isPostAuthor = member.equals(writer);
                String imageFileName = Optional.ofNullable(writer.getImageFileName()).orElse("");
                String imageUrl = imageFileName.isEmpty() ? ""
                    : s3FileService.getGetPreSignedUrl("profiles", imageFileName);
                return new PostViewResponse(post, isPostAuthor, imageUrl, photoViewList);
            })
            .toList();
    }

}
