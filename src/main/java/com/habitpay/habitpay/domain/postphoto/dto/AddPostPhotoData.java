package com.habitpay.habitpay.domain.postphoto.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AddPostPhotoData {
    private Long viewOrder;
    private String imageExtension;
    private Long contentLength;
}