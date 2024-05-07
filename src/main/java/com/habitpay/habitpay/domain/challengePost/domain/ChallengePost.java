package com.habitpay.habitpay.domain.challengePost.domain;

import com.habitpay.habitpay.domain.model.BaseTime;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "challenge_post")
public class ChallengePost extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // todo : challenge_enrollment_id와 외래키 연결
    @Column(nullable = false)
    private Long challengeEnrollmentId;

    @Column()
    private String content;

    @Column(nullable = false)
    private boolean isAnnouncement;

    @Builder
    public ChallengePost(Long challengeEnrollmentId, String content, boolean isAnnouncement) {
        this.challengeEnrollmentId = challengeEnrollmentId;
        this.content = content;
        this.isAnnouncement = isAnnouncement;
    }

    public ChallengePost modifyContent(String modifiedContent) {
        this.content = modifiedContent;
        return this;
    }
}
