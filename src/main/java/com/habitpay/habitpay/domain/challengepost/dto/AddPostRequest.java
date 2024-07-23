package com.habitpay.habitpay.domain.challengepost.dto;

import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.postphoto.dto.AddPostPhotoData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddPostRequest {
    private String content;
    private Boolean isAnnouncement;
    private List<AddPostPhotoData> photos;

    public ChallengePost toEntity(ChallengeEnrollment enrollment) {
        return ChallengePost.builder()
                .content(content)
                .isAnnouncement(isAnnouncement)
                .enrollment(enrollment)
                .build();
    }
}
