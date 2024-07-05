package com.habitpay.habitpay.domain.member.dto;

import com.habitpay.habitpay.domain.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
public class MemberResponse {
    private String nickname;
    private String imageUrl;
}
