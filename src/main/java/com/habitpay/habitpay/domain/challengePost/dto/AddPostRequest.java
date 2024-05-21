package com.habitpay.habitpay.domain.challengePost.dto;

import com.habitpay.habitpay.domain.challengePost.domain.ChallengePost;
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
    // todo : postPhto 브랜치와 머지한 후 추가하기
    // private List<PostPhtoData> photos;

    public ChallengePost toEntity(Long challengeEnrollmentId) {
        return ChallengePost.builder()
                .content(content)
                .isAnnouncement(isAnnouncement)
                .challengeEnrollmentId(challengeEnrollmentId)
                .build();
    }
}
