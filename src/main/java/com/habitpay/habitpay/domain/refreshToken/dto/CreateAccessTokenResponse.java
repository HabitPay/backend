package com.habitpay.habitpay.domain.refreshToken.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class CreateAccessTokenResponse {
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private String refreshToken;
    // todo : 추후 권한 처리 시 필요할지도?
//    private String scope;
}
