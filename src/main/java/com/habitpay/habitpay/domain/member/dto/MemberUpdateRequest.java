package com.habitpay.habitpay.domain.member.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberUpdateRequest {
    private String nickname;
    private String imageExtension;
    private Long contentLength;
}