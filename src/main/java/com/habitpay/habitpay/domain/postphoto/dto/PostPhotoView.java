package com.habitpay.habitpay.domain.postphoto.dto;

import com.habitpay.habitpay.domain.postphoto.application.PostPhotoService;
import com.habitpay.habitpay.domain.postphoto.domain.PostPhoto;
import com.habitpay.habitpay.global.config.aws.S3FileService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PostPhotoView {
    private Long postPhotoId;
    private Long viewOrder;
    private String imageUrl;
}
