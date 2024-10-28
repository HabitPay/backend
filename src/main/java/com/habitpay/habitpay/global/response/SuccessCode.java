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
    DELETE_MEMBER_ACCOUNT_SUCCESS("정상적으로 탈퇴되었습니다."),

    // Challenge
    CREATE_CHALLENGE_SUCCESS("정상적으로 챌린지가 생성되었습니다."),
    PATCH_CHALLENGE_SUCCESS("정상적으로 챌린지 정보 수정이 반영되었습니다."),
    ENROLL_CHALLENGE_SUCCESS("정상적으로 챌린지에 등록했습니다."),
    CANCEL_CHALLENGE_ENROLLMENT_SUCCESS("정상적으로 챌린지 등록을 취소했습니다."),
    GIVING_UP_CHALLENGE("정상적으로 챌린지 중도 포기 처리가 되었습니다."),
    DELETE_CHALLENGE_SUCCESS("정상적으로 챌린지를 삭제했습니다."),

    // Post
    CREATE_POST_SUCCESS("정상적으로 포스트를 생성했습니다."),
    PATCH_POST_SUCCESS("정상적으로 포스트를 수정했습니다."),
    DELETE_POST_SUCCESS("정상적으로 포스트를 삭제했습니다."),

    // Token
    REFRESH_TOKEN_SUCCESS("새로운 액세스 토큰 및 리프레시 토큰이 성공적으로 발급되었습니다.");

    private final String message;

}
