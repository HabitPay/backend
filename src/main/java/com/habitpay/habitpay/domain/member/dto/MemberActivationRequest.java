package com.habitpay.habitpay.domain.member.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberActivationRequest {
    private String nickname;
}
