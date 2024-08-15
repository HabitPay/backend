package com.habitpay.habitpay.domain.challengeparticipationrecord.domain;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.model.BaseTime;
import com.habitpay.habitpay.domain.participationstat.domain.ParticipationStat;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
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
    private LocalDate targetDate;

    @OneToOne
    @JoinColumn(name = "challenge_post_id")
    private ChallengePost challengePost;

    @Builder
    public ChallengeParticipationRecord(
            ChallengeEnrollment enrollment,
            ParticipationStat stat,
            LocalDate targetDate) {
        this.challenge = enrollment.getChallenge();
        this.challengeEnrollment = enrollment;
        this.participationStat = stat;
        this.targetDate = targetDate;
    }

}
