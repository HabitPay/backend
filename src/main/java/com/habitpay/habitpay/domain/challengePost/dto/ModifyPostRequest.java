package com.habitpay.habitpay.domain.challengePost.dto;

import com.habitpay.habitpay.domain.postPhoto.dto.AddPostPhotoData;
import com.habitpay.habitpay.domain.postPhoto.dto.ModifyPostPhotoData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ModifyPostRequest {
    private String content;
    private Boolean isAnnouncement;
    private List<AddPostPhotoData> newPhotos;
    private List<ModifyPostPhotoData> modifiedPhotos;
    private List<Long> deletedPhotoIds;
}
