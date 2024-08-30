package com.habitpay.habitpay.domain.challengepost.domain;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.model.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Table(name = "challenge_post")
public class ChallengePost extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_enrollment_id")
    private ChallengeEnrollment challengeEnrollment;

    @Column()
    private String content;

    @Column(nullable = false)
    private Boolean isAnnouncement;

    @Builder
    public ChallengePost(
            Challenge challenge,
            ChallengeEnrollment enrollment,
            String content,
            boolean isAnnouncement) {
        this.challenge = challenge;
        this.challengeEnrollment = enrollment;
        this.content = content;
        this.isAnnouncement = isAnnouncement;
    }

    public Member getWriter() {
        return challengeEnrollment.getMember();
    }

    public void modifyPostContent(String modifiedContent) {
        this.content = modifiedContent;
    }

    public void modifyPostIsAnnouncement(boolean isAnnouncement) {
        this.isAnnouncement = isAnnouncement;
    }
}
