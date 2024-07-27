package com.habitpay.habitpay.global.error.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Common
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "Entity Not Found"),
    INVALID_VALUE(HttpStatus.BAD_REQUEST, "Invalid Input Value"),

    // Member
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    INVALID_NICKNAME_RULE(HttpStatus.BAD_REQUEST, "닉네임 규칙에 맞지 않습니다. (규칙: 길이 2~15자, 특수문자 제외)"),
    DUPLICATED_NICKNAME(HttpStatus.BAD_REQUEST, "이전과 동일한 닉네임으로 변경할 수 없습니다.");

    private HttpStatus status;
    private final String message;

}
