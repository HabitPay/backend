package com.habitpay.habitpay.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErrorMessageResponse {
    private String error;
    private String errorDescription;
//    private String errorUri;
}
