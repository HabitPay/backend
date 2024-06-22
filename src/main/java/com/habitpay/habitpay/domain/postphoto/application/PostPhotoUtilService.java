package com.habitpay.habitpay.domain.postphoto.application;

import com.habitpay.habitpay.domain.postphoto.domain.PostPhoto;
import com.habitpay.habitpay.domain.postphoto.dto.PostPhotoView;
import com.habitpay.habitpay.global.config.aws.S3FileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public String getImageUrl(PostPhoto postPhoto) {
        // todo : enrollment 엔티티 만들어지면, 통해서 challengeId 가져오기
        //      : 목표 경로 'challenges/{challenge_id}/{post_id}'
        return s3FileService.getGetPreSignedUrl(POST_PHOTOS_PREFIX, postPhoto.getImageFileName());
    }

    // todo : 사진 순서 데이터 어떻게 오는지 확인하고 작성하기
    @Transactional
    public void changeViewOrder(Long photoId, Long newViewOrder) {
        PostPhoto photo = postPhotoSearchService.findById(photoId);
        photo.changeViewOrder(newViewOrder);
    }

    public void authorizePhotoUploader(PostPhoto postPhoto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("email : " + email);
//        if (!postPhoto.getUploader().getEmail().equals(email)) {
//            throw new IllegalArgumentException("(for debug) not authorized");
//        }
    }
}
