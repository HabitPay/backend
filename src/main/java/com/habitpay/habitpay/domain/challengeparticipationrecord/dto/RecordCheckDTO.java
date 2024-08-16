package com.habitpay.habitpay.domain.challengeparticipationrecord.dto;

import com.habitpay.habitpay.domain.challengeparticipationrecord.domain.ChallengeParticipationRecord;
import com.habitpay.habitpay.domain.participationstat.domain.ParticipationStat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class RecordCheckDTO {
    private ChallengeParticipationRecord record;
    private int FeePerAbsence;
}
