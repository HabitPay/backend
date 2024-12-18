package com.habitpay.habitpay.domain.challengeenrollment.domain;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.participationstat.domain.ParticipationStat;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "participation_stat_id")
    private ParticipationStat participationStat;

    @Column(nullable = false)
    private boolean isGivenUp;

    @Column(nullable = false)
    private ZonedDateTime enrolledDate;

    @Column()
    private ZonedDateTime givenUpDate;

    @Builder
    public ChallengeEnrollment(Challenge challenge, Member member) {
        this.challenge = challenge;
        this.member = member;
        this.isGivenUp = false;
        this.enrolledDate = ZonedDateTime.now();
    }

    public static ChallengeEnrollment of(Member member, Challenge challenge) {
        return ChallengeEnrollment.builder()
            .challenge(challenge)
            .member(member)
            .build();
    }
}
