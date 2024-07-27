package com.habitpay.habitpay.global.error;

import com.habitpay.habitpay.global.error.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

    private String code;
    private String message;
//    private List<FieldError> errors;

    private ErrorResponse(final ErrorCode code) {
        this.code = code.name();
        this.message = code.getMessage();
    }

    public static ErrorResponse of(final ErrorCode code) {
        return new ErrorResponse(code);
    }
}
