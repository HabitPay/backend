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

    public void setSuccessCount() {
        ++this.successCount;
    }

    public void setFailureCountAndTotalFee(int fee) {
        ++this.failureCount;
        this.totalFee += fee;
    }
}
