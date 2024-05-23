package com.habitpay.habitpay.domain.challenge.domain;

import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.model.BaseTime;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;

@Getter
@NoArgsConstructor
@Entity
@Slf4j
@Table(name = "challenge")
public class Challenge extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member host;

    @Column(nullable = false)
    private String title;

    @Column()
    private String description;

    @Column(nullable = false)
    private ZonedDateTime startDate;

    @Column(nullable = false)
    private ZonedDateTime endDate;

    @Column()
    private ZonedDateTime stopDate;

    @Column(nullable = false)
    private int numberOfParticipants;

    @Column(nullable = false)
    private byte participatingDays;

    @Column(nullable = false)
    private int feePerAbsence;

    @Column(nullable = false)
    private int totalAbsenceFee;

    @Column(nullable = false)
    private boolean isPaidAll;

    @Builder
    public Challenge(Member member, String title, String description, ZonedDateTime startDate, ZonedDateTime endDate,
                     byte participatingDays, int feePerAbsence) {
        this.host = member;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.participatingDays = participatingDays;
        this.feePerAbsence = feePerAbsence;
    }

    public void updateChallengeDescription(String description) {
        this.description = description;
    }
}