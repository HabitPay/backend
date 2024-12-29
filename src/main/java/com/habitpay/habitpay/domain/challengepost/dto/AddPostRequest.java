package com.habitpay.habitpay.domain.challengepost.dto;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.postphoto.dto.AddPostPhotoData;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddPostRequest {

    @Size(max = 1000, message = "본문 길이는 최대 {max}자 입니다.")
    private String content;

    private Boolean isAnnouncement;
    private List<AddPostPhotoData> photos;

    public ChallengePost toEntity(Challenge challenge, ChallengeEnrollment enrollment) {
        return ChallengePost.builder()
            .content(content)
            .isAnnouncement(isAnnouncement)
            .challenge(challenge)
            .enrollment(enrollment)
            .build();
    }
}
