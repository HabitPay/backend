package com.habitpay.habitpay.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessCode {

    // common
    NO_MESSAGE(""),

    // Member
    NICKNAME_UPDATE_SUCCESS("닉네임 변경에 성공했습니다."),
    PROFILE_IMAGE_UPDATE_SUCCESS("프로필 이미지 변경에 성공했습니다."),

    // Challenge
    ENROLL_CHALLENGE_SUCCESS("정상적으로 챌린지에 등록했습니다."),
    CANCEL_CHALLENGE_ENROLLMENT_SUCCESS("정상적으로 챌린지 등록을 취소했습니다."),
    DELETE_CHALLENGE_SUCCESS("정상적으로 챌린지를 삭제했습니다."),

    // Post
    DELETE_POST_SUCCESS("정상적으로 포스트를 삭제했습니다.");

    private final String message;

}
