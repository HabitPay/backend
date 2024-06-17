package com.habitpay.habitpay.domain.challengeparticipationrecord.domain;

import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.model.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "challenge_participation_record")
public class ChallengeParticipationRecord extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn
    private ChallengeEnrollment enrollment;

    @OneToOne
    @JoinColumn
    private ChallengePost post;

    @Builder
    public ChallengeParticipationRecord(ChallengeEnrollment enrollment, ChallengePost post) {
        this.enrollment = enrollment;
        this.post = post;
    }
}
