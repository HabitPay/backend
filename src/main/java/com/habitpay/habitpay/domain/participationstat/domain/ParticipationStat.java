package com.habitpay.habitpay.domain.participationstat.domain;

import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "participation_stat")
public class ParticipationStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn
    private ChallengeEnrollment challengeEnrollment;

    @Column(nullable = false)
    private Long successCount;

    @Column(nullable = false)
    private Long failureCount;

    @Column(nullable = false)
    private Long totalFee;

    @Builder
    public ParticipationStat(ChallengeEnrollment enrollment) {
        this.challengeEnrollment = enrollment;
        this.successCount = 0L;
        this.failureCount = 0L;
        this.totalFee = 0L;
    }
}
