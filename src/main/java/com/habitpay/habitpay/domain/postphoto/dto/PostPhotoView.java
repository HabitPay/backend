package com.habitpay.habitpay.domain.postphoto.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PostPhotoView {
    private Long postPhotoId;
    private Long viewOrder;
    private String imageUrl;
}
