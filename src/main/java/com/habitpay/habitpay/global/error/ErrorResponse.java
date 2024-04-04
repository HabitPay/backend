package com.habitpay.habitpay.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorResponse {
    UNSUPPORTED_IMAGE_EXTENSION("지원하지 않는 이미지 확장자입니다."),
    UNAUTHORIZED("유효한 토큰이 아닙니다. 다시 로그인 해주세요.");

    private final String message;
}
