package com.habitpay.habitpay.domain.refreshToken.dto;

import lombok.Getter;
import lombok.Setter;

// todo: refresh token을 요청 헤더로 받을 경우, 필요 없어지는 dto
@Getter
@Setter
public class CreateAccessTokenRequest {
    private String refreshToken;
}
