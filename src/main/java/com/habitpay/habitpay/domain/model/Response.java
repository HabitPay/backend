package com.habitpay.habitpay.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Response {
    PROFILE_UPDATE_SUCCESS("프로필 업데이트에 성공했습니다.");

    private final String message;

}
