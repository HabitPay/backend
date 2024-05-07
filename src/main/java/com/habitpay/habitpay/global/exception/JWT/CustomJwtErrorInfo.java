package com.habitpay.habitpay.global.exception.JWT;

import lombok.*;

@AllArgsConstructor
@Getter
public enum CustomJwtErrorInfo {
    BAD_REQUEST("invalid_request"), // 400
    UNAUTHORIZED("invalid_token"), // 401
    FORBIDDEN("insufficient_scope"); // 403

    private final String errorMessage;
}