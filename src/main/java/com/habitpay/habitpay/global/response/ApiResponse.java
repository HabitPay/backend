package com.habitpay.habitpay.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse {
    private String message;

    public static ApiResponse create(String message) {
        return new ApiResponse(message);
    }
}
