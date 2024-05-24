package com.habitpay.habitpay.global.handler.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ExceptionResponse {
    private String error;
    private String errorDescription;
//    private String errorUri;
}
