package com.habitpay.habitpay.domain.challengePost.dto;

import com.habitpay.habitpay.domain.challengePost.domain.ChallengePost;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddPostRequest {
    private String content;
    private boolean isAnnouncement;

    public ChallengePost toEntity(Long challengeEnrollmentId) {
        return ChallengePost.builder()
                .content(content)
                .isAnnouncement(isAnnouncement)
                .challengeEnrollmentId(challengeEnrollmentId)
                .build();
    }
}
