package com.habitpay.habitpay.global.exception;

import lombok.*;

@AllArgsConstructor
@Getter
public enum CustomJwtErrorResponse {
    BAD_REQUEST("invalid_request"), // 400
    UNAUTHORIZED("invalid_token"), // 401
    FORBIDDEN("insufficient_scope"); // 403

    private final String errorMessage;
}
