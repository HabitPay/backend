package com.habitpay.habitpay.domain.postPhoto.application;

import com.habitpay.habitpay.domain.challengePost.domain.ChallengePost;
import com.habitpay.habitpay.domain.postPhoto.dto.PostPhotoView;
import com.habitpay.habitpay.domain.postPhoto.dao.PostPhotoRepository;
import com.habitpay.habitpay.domain.postPhoto.domain.PostPhoto;
import com.habitpay.habitpay.domain.postPhoto.dto.AddPostPhotoData;
import com.habitpay.habitpay.global.config.aws.S3FileService;
import com.habitpay.habitpay.global.error.ErrorResponse;
import com.habitpay.habitpay.global.exception.PostPhoto.CustomPhotoException;
import com.habitpay.habitpay.global.util.ImageUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class PostPhotoService {

    private final S3FileService s3FileService;
    private final PostPhotoRepository postPhotoRepository;

    public final String POST_PHOTOS_PREFIX = "postPhotos";

    // todo : @Transactional 적절한지 확인하고 추가
    public List<String> save(ChallengePost post, List<AddPostPhotoData> photos) {

        if (photos == null) {
            return null;
        }

        List<String> urlList = new ArrayList<>();

        for (AddPostPhotoData photo : photos) {
            String preSignedUrl = savePhoto(post, photo);
            urlList.add(preSignedUrl);
        }

        return urlList;
    }

    public PostPhoto findById(Long id) {
        return postPhotoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("(for debugging) not found : " + id));
    }

    public List<PostPhoto> findAllByPost(ChallengePost post) {
        return postPhotoRepository.findAllByPost(post)
                .orElseThrow(() -> new NoSuchElementException("(for debugging) not found challenge post : " + post.getId()));
    }

    public void delete(Long id) {
        PostPhoto postPhoto = postPhotoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("(for debugging) not found : " + id));

        authorizePhotoUploader(postPhoto);

        s3FileService.deleteImage(POST_PHOTOS_PREFIX, findById(id).getImageFileName());
        postPhotoRepository.delete(postPhoto);
    }

    public List<PostPhotoView> makePhotoViewList(List<PostPhoto> photoList) {
        List<PostPhotoView> photoViewList = new ArrayList<>();

        // getImageUrl 메서드를 써야하므로, PostPhotoView에 생성자 만들기 어려움
        photoList.forEach(photo -> photoViewList.add(new PostPhotoView(photo.getId(), photo.getViewOrder(), getImageUrl(photo))));

        return photoViewList;
    }

    private String savePhoto(ChallengePost post, AddPostPhotoData photo) {
        String imageExtension = photo.getImageExtension();
        Long contentLength = photo.getContentLength();

        if (!ImageUtil.isValidFileSize(contentLength)) {
            throw new CustomPhotoException(HttpStatus.PAYLOAD_TOO_LARGE, ErrorResponse.IMAGE_CONTENT_TOO_LARGE, "image no." + photo.getViewOrder());
        }

        if (!ImageUtil.isValidImageExtension(imageExtension)) {
            throw new CustomPhotoException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ErrorResponse.UNSUPPORTED_IMAGE_EXTENSION, "image no." + photo.getViewOrder());
        }

        String randomFileName = UUID.randomUUID().toString();
        String savedFileName = String.format("%s.%s", randomFileName, imageExtension);
        log.info("[save] savedFileName: {}", savedFileName);

        postPhotoRepository.save(PostPhoto.builder()
                .post(post)
                .imageFileName(savedFileName)
                .viewOrder(photo.getViewOrder())
                .build());

        return s3FileService.getPutPreSignedUrl(POST_PHOTOS_PREFIX, savedFileName, imageExtension, contentLength);
    }

    private String getImageUrl(PostPhoto postPhoto) {
        return s3FileService.getGetPreSignedUrl(POST_PHOTOS_PREFIX, postPhoto.getImageFileName());
    }

    // todo : 사진 순서 데이터 어떻게 오는지 확인하고 작성하기
    @Transactional
    public void changeViewOrder(Long photoId, Long newViewOrder) {
        PostPhoto photo = findById(photoId);
        photo.changeViewOrder(newViewOrder);
    }

    private static void authorizePhotoUploader(PostPhoto postPhoto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("email : " + email);
//        if (!postPhoto.getUploader().getEmail().equals(email)) {
//            throw new IllegalArgumentException("(for debug) not authorized");
//        }
    }
}
