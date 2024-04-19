package com.habitpay.habitpay.domain.refreshToken.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErrorCreateAccessTokenResponse {
    private String error;
    private String errorDescription;
//    private String errorUri;
}
