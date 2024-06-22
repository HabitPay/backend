package com.habitpay.habitpay.domain.postphoto.application;

import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.postphoto.dao.PostPhotoRepository;
import com.habitpay.habitpay.domain.postphoto.domain.PostPhoto;
import com.habitpay.habitpay.global.config.aws.S3FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
@Slf4j
public class PostPhotoDeleteService {

    private final S3FileService s3FileService;
    private final PostPhotoRepository postPhotoRepository;
    private final PostPhotoSearchService postPhotoSearchService;
    private final PostPhotoUtilService postPhotoUtilService;

    public void delete(Long id) {
        PostPhoto postPhoto = postPhotoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("(for debugging) not found : " + id));

        postPhotoUtilService.authorizePhotoUploader(postPhoto);

        // todo : enrollment 엔티티 만들어지면, 통해서 challengeId 가져오기
        //      : 목표 경로 'challenges/{challenge_id}/{post_id}'
        s3FileService.deleteImage(postPhotoUtilService.POST_PHOTOS_PREFIX, postPhotoSearchService.findById(id).getImageFileName());
        postPhotoRepository.delete(postPhoto);
    }

    public void deleteAllByPost(ChallengePost post) {
        List<PostPhoto> photoList = postPhotoRepository.findAllByPost(post)
                .orElseThrow(() -> new NoSuchElementException("(for debugging) not found post : " + post.getId()));

        photoList.forEach(photo -> {delete(photo.getId());});
    }
}
