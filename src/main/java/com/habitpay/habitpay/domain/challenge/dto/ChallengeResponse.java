package com.habitpay.habitpay.domain.challenge.dto;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.member.domain.Member;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.Optional;

@Getter
public class ChallengeResponse {
    private String title;
    private String description;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private byte participatingDays;
    private int feePerAbsence;
    private String hostNickname;
    private String hostProfileImage;
    private Boolean isHost;
    private Boolean isEnrolledMember;

    public ChallengeResponse(Member member, Challenge challenge, Optional<ChallengeEnrollment> optionalChallengeEnrollment) {
        this.title = challenge.getTitle();
        this.description = challenge.getDescription();
        this.startDate = challenge.getStartDate();
        this.endDate = challenge.getEndDate();
        this.participatingDays = challenge.getParticipatingDays();
        this.feePerAbsence = challenge.getFeePerAbsence();
        this.hostNickname = member.getNickname();
        this.hostProfileImage = member.getImageFileName();
        this.isHost = challenge.getHost().getEmail().equals(member.getEmail());
        if (optionalChallengeEnrollment.isEmpty()) {
            this.isEnrolledMember = false;
        } else {
            ChallengeEnrollment challengeEnrollment = optionalChallengeEnrollment.get();
            this.isEnrolledMember = challengeEnrollment.getChallenge().getId().equals(challenge.getId());
        }
    }
}
