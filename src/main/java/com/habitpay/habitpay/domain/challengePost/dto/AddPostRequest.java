package com.habitpay.habitpay.domain.challengePost.dto;

import com.habitpay.habitpay.domain.challengePost.domain.ChallengePost;
import com.habitpay.habitpay.domain.postPhoto.domain.PostPhoto;
import com.habitpay.habitpay.domain.postPhoto.dto.PostPhotoData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddPostRequest {
    private String content;
    private boolean isAnnouncement;
     private List<PostPhotoData> photos;

    public ChallengePost toEntity(Long challengeEnrollmentId) {
        return ChallengePost.builder()
                .content(content)
                .isAnnouncement(isAnnouncement)
                .challengeEnrollmentId(challengeEnrollmentId)
                .build();
    }
}
