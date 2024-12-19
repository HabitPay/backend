package com.habitpay.habitpay.domain.postphoto.application;

import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.postphoto.dao.PostPhotoRepository;
import com.habitpay.habitpay.domain.postphoto.domain.PostPhoto;
import com.habitpay.habitpay.domain.postphoto.dto.AddPostPhotoData;
import com.habitpay.habitpay.global.config.aws.S3FileService;
import com.habitpay.habitpay.global.util.ImageUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class PostPhotoCreationService {

    private final S3FileService s3FileService;
    private final PostPhotoRepository postPhotoRepository;
    private final PostPhotoUtilService postPhotoUtilService;
    private final ImageUtil imageUtil;

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

        imageUtil.validateImageFormat(photo.getContentLength(), photo.getImageExtension());

        String savedFileName = String.format("%s.%s", UUID.randomUUID(), photo.getImageExtension());
        log.info("[save] savedFileName: {}", savedFileName);

        PostPhoto postPhoto = postPhotoRepository.save(PostPhoto.builder()
            .post(post)
            .imageFileName(savedFileName)
            .viewOrder(photo.getViewOrder())
            .build());

        String targetUrl = postPhotoUtilService.makeS3TargetPath(postPhoto);
        
        return s3FileService.getPutPreSignedUrl(targetUrl, savedFileName, photo.getImageExtension(),
            photo.getContentLength());
    }

}
