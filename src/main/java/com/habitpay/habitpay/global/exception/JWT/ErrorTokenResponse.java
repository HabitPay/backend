package com.habitpay.habitpay.global.exception.JWT;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErrorTokenResponse {
    private String error;
    private String errorDescription;
//    private String errorUri;
}
