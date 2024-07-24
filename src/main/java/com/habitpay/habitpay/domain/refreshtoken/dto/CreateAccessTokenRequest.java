package com.habitpay.habitpay.domain.refreshtoken.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateAccessTokenRequest {
    private String grantType;
    private String refreshToken;
}
