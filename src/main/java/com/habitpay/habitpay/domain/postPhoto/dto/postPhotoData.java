package com.habitpay.habitpay.domain.postPhoto.dto;

import lombok.Getter;

@Getter
public class postPhotoData {
    private Long postId;
    private Long viewOrder;
    private String imageExtension;
    private Long contentLength;
}