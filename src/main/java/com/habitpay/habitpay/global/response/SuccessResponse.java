package com.habitpay.habitpay.global.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SuccessResponse<T> {
    // TODO: String 대신 SuccessCode 바꾸기
    private final String message;
    private final T data;

    public static <T> SuccessResponse<T> of(String message, T data) {
        return SuccessResponse.<T>builder()
                .message(message)
                .data(data)
                .build();
    }

    public static <T> SuccessResponse<T> of(SuccessCode successCode, T data) {
        return SuccessResponse.<T>builder()
                .message(successCode.getMessage())
                .data(data)
                .build();
    }

    public static <T> SuccessResponse<T> of(SuccessCode successCode) {
        return SuccessResponse.<T>builder()
                .message(successCode.getMessage())
                .build();
    }
}
