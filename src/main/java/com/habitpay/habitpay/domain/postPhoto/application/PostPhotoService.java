package com.habitpay.habitpay.domain.postPhoto.application;

import com.habitpay.habitpay.domain.postPhoto.dao.PostPhotoRepository;
import com.habitpay.habitpay.domain.postPhoto.domain.PostPhoto;
import com.habitpay.habitpay.domain.postPhoto.dto.PostPhotoData;
import com.habitpay.habitpay.global.config.aws.S3FileService;
import com.habitpay.habitpay.global.error.ErrorResponse;
import com.habitpay.habitpay.global.exception.PostPhoto.CustomPhotoException;
import com.habitpay.habitpay.global.util.ImageUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Service
@Slf4j
public class PostPhotoService {

    private final S3FileService s3FileService;
    private final PostPhotoRepository postPhotoRepository;

    public final String POST_PHOTOS_PREFIX = "postPhotos";

    // todo : @Transaction 적절한지 확인하고 추가 (for 중간에 잘못됐을 경우 모든 걸 없던 걸로 되돌리는?) / 필요 없을지도?
    public List<String> save(List<PostPhotoData> photos) {
        List<String> urlList = new ArrayList<>();

        for (PostPhotoData photo : photos) {
            String imageExtension = photo.getImageExtension();
            Long contentLength = photo.getContentLength();

            if (!ImageUtil.isValidFileSize(contentLength)) {
                throw new CustomPhotoException(HttpStatus.PAYLOAD_TOO_LARGE, ErrorResponse.IMAGE_CONTENT_TOO_LARGE, " image no." + photo.getViewOrder());
            }

            if (!ImageUtil.isValidImageExtension(imageExtension)) {
                throw new CustomPhotoException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ErrorResponse.UNSUPPORTED_IMAGE_EXTENSION, " image no." + photo.getViewOrder());
            }

            String randomFileName = UUID.randomUUID().toString();
            String savedFileName = String.format("%s.%s", randomFileName, imageExtension);
            log.info("[save] savedFileName: {}", savedFileName);

            postPhotoRepository.save(PostPhoto.builder()
                    .postId(photo.getPostId())
                    .imageFileName(savedFileName)
                    .viewOrder(photo.getViewOrder())
                    .build());

            String preSignedUrl = s3FileService.getPutPreSignedUrl(POST_PHOTOS_PREFIX, savedFileName, imageExtension, contentLength);

            urlList.add(preSignedUrl);
        }

        return urlList;
    }

    public PostPhoto findById(Long id) {
        return postPhotoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("(for debugging) not found : " + id));
    }

    // todo : post 별로 찾아주는 메서드 (진행 중)
    public List<PostPhoto> findAllByPost(Long postId) {
        return postPhotoRepository.findAll();
    }

    public void delete(Long id) {
        PostPhoto postPhoto = postPhotoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("(for debugging) not found : " + id));

        authorizePhotoUploader(postPhoto);
        // todo : aws에서도 이미지 파일 삭제하기
        postPhotoRepository.delete(postPhoto);
    }

    public String getImageUrl(PostPhoto postPhoto) {
        return s3FileService.getGetPreSignedUrl(POST_PHOTOS_PREFIX, postPhoto.getImageFileName());
    }

    // todo : 사진 순서 데이터 어떻게 오는지 확인하고 작성하기
//    @Transactional
//    public PostPhoto changeViewOrder() {}

    private static void authorizePhotoUploader(PostPhoto postPhoto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("email : " + email);
//        if (!postPhoto.getUploader().getEmail().equals(email)) {
//            throw new IllegalArgumentException("(for debug) not authorized");
//        }
    }
}
