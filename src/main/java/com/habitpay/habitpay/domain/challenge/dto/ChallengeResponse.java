package com.habitpay.habitpay.domain.challenge.dto;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.member.domain.Member;
import lombok.Getter;

import java.time.ZonedDateTime;

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
    private boolean isHost;

    public ChallengeResponse(Member host, Challenge challenge) {
        this.title = challenge.getTitle();
        this.description = challenge.getDescription();
        this.startDate = challenge.getStartDate();
        this.endDate = challenge.getEndDate();
        this.participatingDays = challenge.getParticipatingDays();
        this.feePerAbsence = challenge.getFeePerAbsence();
        this.hostNickname = host.getNickname();
        this.hostProfileImage = host.getImageFileName();
        this.isHost = challenge.getHost().getEmail().equals(host.getEmail());
    }
}
