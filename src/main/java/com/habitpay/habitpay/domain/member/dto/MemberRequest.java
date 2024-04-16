package com.habitpay.habitpay.domain.member.dto;

import lombok.Getter;

@Getter
public class MemberRequest {
    private String nickname;
    private String imageExtension;
    private Long contentLength;
}
