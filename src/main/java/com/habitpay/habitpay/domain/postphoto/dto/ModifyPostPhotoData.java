package com.habitpay.habitpay.domain.postphoto.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ModifyPostPhotoData {
    private Long photoId;
    private Long viewOrder;
}
