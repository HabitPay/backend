package com.habitpay.habitpay.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberResponse {
    private String nickname;
    private String imageUrl;
}
