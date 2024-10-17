package com.habitpay.habitpay.domain.challenge.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Builder
public class ChallengeRecordsResponse {
    List<ZonedDateTime> successRecordList;
    List<ZonedDateTime> failRecordList;
    List<ZonedDateTime> upcomingRecordList;
}
