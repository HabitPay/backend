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
    DUPLICATED_NICKNAME(HttpStatus.BAD_REQUEST, "이전과 동일한 닉네임으로 변경할 수 없습니다."),
    PROFILE_IMAGE_SIZE_TOO_LARGE(HttpStatus.BAD_REQUEST, "이미지 파일의 크기가 제한을 초과했습니다. (최대 1MB)"),
    UNSUPPORTED_IMAGE_EXTENSION(HttpStatus.BAD_REQUEST, "지원하지 않는 이미지 확장자입니다. (png, jpg, jpeg 만 가능)"),

    // Challenge
    CHALLENGE_NOT_FOUND(HttpStatus.NOT_FOUND, "챌린지가 존재하지 않습니다."),
    CHALLENGE_START_TIME_INVALID(HttpStatus.BAD_REQUEST, "챌린지 시작 시간은 현재 시간 이후만 가능합니다.");
    private HttpStatus status;
    private final String message;

}
