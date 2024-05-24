package com.habitpay.habitpay.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ExceptionResponse {
    private String error;
    private String errorDescription;
//    private String errorUri;
}
