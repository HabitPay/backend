package com.habitpay.habitpay.domain.challengepost.domain;

import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.model.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.NoSuchElementException;

@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "challenge_post")
public class ChallengePost extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @Column(name = "challenge_enrollment_id")
    private ChallengeEnrollment enrollment;

    @Column()
    private String content;

    @Column(nullable = false)
    private Boolean isAnnouncement;

    @Builder
    public ChallengePost(ChallengeEnrollment enrollment, String content, boolean isAnnouncement) {
        this.enrollment = enrollment;
        this.content = content;
        this.isAnnouncement = isAnnouncement;
    }

    public Member getWriter() {
        return enrollment.getMember();
    }

    public void modifyPostContent(String modifiedContent) {
        this.content = modifiedContent;
    }

    public void modifyPostIsAnnouncement(boolean isAnnouncement) {
        this.isAnnouncement = isAnnouncement;
    }
}
