package com.habitpay.habitpay.domain.challengepost.domain;

import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.model.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    private Boolean isAnnouncement;

    @Builder
    public ChallengePost(Long challengeEnrollmentId, String content, boolean isAnnouncement) {
        this.challengeEnrollmentId = challengeEnrollmentId;
        this.content = content;
        this.isAnnouncement = isAnnouncement;
    }

    public void modifyPostContent(String modifiedContent) {
        this.content = modifiedContent;
    }

    public void modifyPostIsAnnouncement(boolean isAnnouncement) {
        this.isAnnouncement = isAnnouncement;
    }
}
