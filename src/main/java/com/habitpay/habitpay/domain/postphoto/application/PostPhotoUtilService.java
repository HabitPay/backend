package com.habitpay.habitpay.domain.postphoto.application;

import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.postphoto.domain.PostPhoto;
import com.habitpay.habitpay.domain.postphoto.dto.PostPhotoView;
import com.habitpay.habitpay.global.config.aws.S3FileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class PostPhotoUtilService {

    private final S3FileService s3FileService;
    private final PostPhotoSearchService postPhotoSearchService;

    public final String POST_PHOTOS_PREFIX = "challenges";

    public List<PostPhotoView> makePhotoViewList(List<PostPhoto> photoList) {
        List<PostPhotoView> photoViewList = new ArrayList<>();
        photoList.forEach(photo -> photoViewList.add(new PostPhotoView(photo.getId(), photo.getViewOrder(), getImageUrl(photo))));

        return photoViewList;
    }

    public String getImageUrl(PostPhoto photo) {
        String targetUrl = this.makeS3TargetPath(photo);

        return s3FileService.getGetPreSignedUrl(targetUrl, photo.getImageFileName());
    }

    public String makeS3TargetPath(PostPhoto photo) {
        ChallengePost post = photo.getChallengePost();

        // result : challenges/{challenge_id}/{post_id}
        return this.POST_PHOTOS_PREFIX +
                "/" +
                post.getChallengeEnrollment().getChallenge().getId() +
                "/" +
                post.getId();
    }

    // todo : 사진 순서 데이터 어떻게 오는지 확인하고 작성하기
    @Transactional
    public void changeViewOrder(Long photoId, Long newViewOrder) {
        PostPhoto photo = postPhotoSearchService.getPostPhotoById(photoId);
        photo.changeViewOrder(newViewOrder);
    }

    public boolean photoBelongToPost(Long photoId, ChallengePost post) {
        return postPhotoSearchService.getPostPhotoById(photoId).getChallengePost().equals(post);
    }
}
