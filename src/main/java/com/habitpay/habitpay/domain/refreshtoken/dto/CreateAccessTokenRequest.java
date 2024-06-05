package com.habitpay.habitpay.domain.refreshtoken.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccessTokenRequest {
    private String grantType;
    private String refreshToken;
}
