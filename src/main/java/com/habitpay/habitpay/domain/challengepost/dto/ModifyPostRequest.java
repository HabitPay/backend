package com.habitpay.habitpay.domain.challengepost.dto;

import com.habitpay.habitpay.domain.postphoto.dto.AddPostPhotoData;
import com.habitpay.habitpay.domain.postphoto.dto.ModifyPostPhotoData;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ModifyPostRequest {

    @Size(max = 1000, message = "본문 길이는 최대 {max}자 입니다.")
    private String content;

    private Boolean isAnnouncement;
    private List<AddPostPhotoData> newPhotos;
    private List<ModifyPostPhotoData> modifiedPhotos;
    private List<Long> deletedPhotoIds;
}
