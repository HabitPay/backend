package com.habitpay.habitpay.domain.participationstat.domain;

import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "participation_stat")
public class ParticipationStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_enrollment_id")
    private ChallengeEnrollment challengeEnrollment;

    @Column(nullable = false)
    private int successCount;

    @Column(nullable = false)
    private int failureCount;

    @Column(nullable = false)
    private int totalFee;

    @Builder
    public ParticipationStat(ChallengeEnrollment enrollment) {
        this.challengeEnrollment = enrollment;
        this.successCount = 0;
        this.failureCount = 0;
        this.totalFee = 0;
    }

    public static ParticipationStat of(ChallengeEnrollment enrollment) {
        return ParticipationStat.builder()
            .enrollment(enrollment)
            .build();
    }
}
