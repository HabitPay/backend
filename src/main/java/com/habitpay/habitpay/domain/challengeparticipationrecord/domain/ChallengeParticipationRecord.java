package com.habitpay.habitpay.domain.challengeparticipationrecord.domain;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.model.BaseTime;
import com.habitpay.habitpay.domain.participationstat.domain.ParticipationStat;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

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
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @ManyToOne
    @JoinColumn(name = "challenge_enrollment_id")
    private ChallengeEnrollment challengeEnrollment;

    @ManyToOne
    @JoinColumn(name = "participation_stat_id")
    private ParticipationStat participationStat;

    @Column(nullable = false)
    private Date targetDate;

    @OneToOne
    @JoinColumn(name = "challenge_post_id")
    private ChallengePost challengePost;

    @Builder
    public ChallengeParticipationRecord(ChallengeEnrollment enrollment, ChallengePost post) {
        this.challengeEnrollment = enrollment;
        this.challengePost = post;
    }
}
