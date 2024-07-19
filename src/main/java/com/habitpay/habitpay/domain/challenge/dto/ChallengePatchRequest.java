package com.habitpay.habitpay.domain.challenge.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChallengePatchRequest {
    private String description;
}
