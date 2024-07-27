package com.habitpay.habitpay.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessCode {

    // Member
    NICKNAME_UPDATE_SUCCESS("닉네임 변경에 성공했습니다.");

    private final String message;

}