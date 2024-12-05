package com.habitpay.habitpay.domain.postphoto.application;

import com.habitpay.habitpay.domain.challengepost.application.ChallengePostSearchService;
import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.postphoto.dao.PostPhotoRepository;
import com.habitpay.habitpay.domain.postphoto.domain.PostPhoto;
import com.habitpay.habitpay.global.config.aws.S3FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class PostPhotoDeleteService {

    private final S3FileService s3FileService;
    private final PostPhotoRepository postPhotoRepository;
    private final ChallengePostSearchService challengePostSearchService;
    private final PostPhotoSearchService postPhotoSearchService;
    private final PostPhotoUtilService postPhotoUtilService;

    public void deleteByPost(ChallengePost post) {
        List<PostPhoto> photoList = postPhotoRepository.findAllByChallengePost(post);

        photoList.forEach(photo -> this.deleteById(photo.getId()));
    }

    public void deleteByIds(Long postId, List<Long> photoIdList) {
        ChallengePost post = challengePostSearchService.getChallengePostById(postId);

        Optional.ofNullable(photoIdList).orElse(Collections.emptyList())
        .forEach(photoId -> {
            if (postPhotoUtilService.photoBelongToPost(photoId, post)) {
                this.deleteById(photoId);
            }
        });
    }

    private void deleteById(Long id) {
        PostPhoto photo = postPhotoSearchService.getPostPhotoById(id);
        String targetUrl = postPhotoUtilService.makeS3TargetPath(photo);

        s3FileService.deleteImage(targetUrl, postPhotoSearchService.getPostPhotoById(id).getImageFileName());
        postPhotoRepository.delete(photo);
    }

}
