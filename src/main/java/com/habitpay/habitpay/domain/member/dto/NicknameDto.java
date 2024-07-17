package com.habitpay.habitpay.domain.member.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class NicknameDto {
    private String nickname;
}
