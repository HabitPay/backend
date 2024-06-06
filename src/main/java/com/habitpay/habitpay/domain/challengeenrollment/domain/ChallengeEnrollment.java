package com.habitpay.habitpay.domain.challengeenrollment.domain;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.member.domain.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "challenge_enrollment")
public class ChallengeEnrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private boolean isGivenUp;

    @Column(nullable = false)
    private ZonedDateTime enrolledDate;

    @Column()
    private ZonedDateTime givenUpDate;

    @Column(nullable = false)
    private int successCount;

    @Column(nullable = false)
    private int failureCount;

    @Column(nullable = false)
    private int totalFee;

    @Builder
    public ChallengeEnrollment(Challenge challenge, Member member, ZonedDateTime enrolledDate) {
        this.challenge = challenge;
        this.member = member;
        this.isGivenUp = false;
        this.enrolledDate = enrolledDate;
        this.successCount = 0;
        this.failureCount = 0;
        this.totalFee = 0;
    }
}
