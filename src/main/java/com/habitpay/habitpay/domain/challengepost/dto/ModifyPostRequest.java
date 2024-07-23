package com.habitpay.habitpay.domain.challengepost.dto;

import com.habitpay.habitpay.domain.postphoto.dto.AddPostPhotoData;
import com.habitpay.habitpay.domain.postphoto.dto.ModifyPostPhotoData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ModifyPostRequest {
    private String content;
    private Boolean isAnnouncement;
    private List<AddPostPhotoData> newPhotos;
    private List<ModifyPostPhotoData> modifiedPhotos;
    private List<Long> deletedPhotoIds;
}
