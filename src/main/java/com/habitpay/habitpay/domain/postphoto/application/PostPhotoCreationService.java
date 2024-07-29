package com.habitpay.habitpay.domain.postphoto.application;

import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.postphoto.dao.PostPhotoRepository;
import com.habitpay.habitpay.domain.postphoto.domain.PostPhoto;
import com.habitpay.habitpay.domain.postphoto.dto.AddPostPhotoData;
import com.habitpay.habitpay.domain.postphoto.exception.CustomPhotoException;
import com.habitpay.habitpay.global.config.aws.S3FileService;
import com.habitpay.habitpay.global.error.exception.ErrorCode;
import com.habitpay.habitpay.global.error.exception.InvalidValueException;
import com.habitpay.habitpay.global.util.ImageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class PostPhotoCreationService {

    private final S3FileService s3FileService;
    private final PostPhotoRepository postPhotoRepository;
    private final PostPhotoUtilService postPhotoUtilService;

    public List<String> createPhotoUrlList(ChallengePost post, List<AddPostPhotoData> photos) {

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

    private String savePhoto(ChallengePost post, AddPostPhotoData photo) {
        String imageExtension = photo.getImageExtension();
        Long contentLength = photo.getContentLength();

        if (!ImageUtil.isValidFileSize(contentLength)) {
            throw new InvalidValueException(String.format("size: %dMB", contentLength / 1024 / 1024), ErrorCode.POST_PHOTO_IMAGE_SIZE_TOO_LARGE);
        }

        if (!ImageUtil.isValidImageExtension(imageExtension)) {
            throw new InvalidValueException(String.format("extension: %s", imageExtension), ErrorCode.UNSUPPORTED_IMAGE_EXTENSION);
        }

        String randomFileName = UUID.randomUUID().toString();
        String savedFileName = String.format("%s.%s", randomFileName, imageExtension);
        log.info("[save] savedFileName: {}", savedFileName);

        PostPhoto postPhoto = postPhotoRepository.save(PostPhoto.builder()
                .post(post)
                .imageFileName(savedFileName)
                .viewOrder(photo.getViewOrder())
                .build());

        String targetUrl = postPhotoUtilService.makeS3TargetPath(postPhoto);
        return s3FileService.getPutPreSignedUrl(targetUrl, savedFileName, imageExtension, contentLength);
    }

}
