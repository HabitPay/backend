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
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad Request"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Invalid token"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Not Allowed to Access or Modify"),
    CONFLICT(HttpStatus.CONFLICT, "Conflict"),

    // JWT
    JWT_CLIENT_HAS_NO_IDEA_ABOUT_GRANT_TYPE(HttpStatus.BAD_REQUEST, "인증 방법을 알 수 없습니다."),
    JWT_REQUEST_IP_AND_LOGIN_IP_NOT_SAME_FOR_REFRESH(HttpStatus.BAD_REQUEST, "로그인한 IP 주소와 요청 IP 주소가 일치하지 않습니다."),
    JWT_REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "리프레시 토큰이 존재하지 않습니다."),
    JWT_FORBIDDEN_TO_MODIFY_OTHERS_POST(HttpStatus.FORBIDDEN, "본인의 포스트만 수정할 수 있습니다."),

    // Member
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    INVALID_NICKNAME_RULE(HttpStatus.BAD_REQUEST, "닉네임 규칙에 맞지 않습니다. (규칙: 길이 2~15자, 특수문자 제외)"),
    DUPLICATED_NICKNAME(HttpStatus.BAD_REQUEST, "이전과 동일한 닉네임으로 변경할 수 없습니다."),
    PROFILE_IMAGE_SIZE_TOO_LARGE(HttpStatus.BAD_REQUEST, "이미지 파일의 크기가 제한을 초과했습니다. (최대 1MB)"),
    UNSUPPORTED_IMAGE_EXTENSION(HttpStatus.BAD_REQUEST, "지원하지 않는 이미지 확장자입니다. (png, jpg, jpeg 만 가능)"),

    // Challenge
    CHALLENGE_NOT_FOUND(HttpStatus.NOT_FOUND, "챌린지가 존재하지 않습니다."),
    CHALLENGE_START_TIME_INVALID(HttpStatus.BAD_REQUEST, "챌린지 시작 시간은 현재 시간 이후만 가능합니다."),
    ONLY_HOST_CAN_MODIFY(HttpStatus.FORBIDDEN, "챌린지 주최자만 수정 가능합니다."),
    DUPLICATED_CHALLENGE_DESCRIPTION(HttpStatus.BAD_REQUEST, "변경 사항이 없습니다."),
    INVALID_CHALLENGE_REGISTRATION_TIME(HttpStatus.BAD_REQUEST, "챌린지 등록 가능 시간이 아닙니다."),
    INVALID_CHALLENGE_CANCELLATION_TIME(HttpStatus.BAD_REQUEST, "챌린지 취소 가능한 시간이 지났습니다."),
    ALREADY_ENROLLED_IN_CHALLENGE(HttpStatus.CONFLICT, "이미 참여한 챌린지 입니다."),
    NOT_ENROLLED_IN_CHALLENGE(HttpStatus.BAD_REQUEST, "참여하지 않은 챌린지 입니다."),
    NOT_ALLOWED_TO_CANCEL_ENROLLMENT_OF_HOST(HttpStatus.BAD_REQUEST, "챌린지 주최자는 참여 취소가 불가능 합니다."),
    NOT_ALLOWED_TO_DELETE_CHALLENGE(HttpStatus.FORBIDDEN, "챌린지 삭제는 챌린지 주최자만 가능합니다."),
  
      // Challenge Participation Record
    RECORD_NOT_FOUND(HttpStatus.NOT_FOUND, "챌린지 참여 기록이 존재하지 않습니다."),

    // Challenge Post
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "포스트가 존재하지 않습니다."),
    ONLY_HOST_CAN_UPLOAD_ANNOUNCEMENT(HttpStatus.FORBIDDEN, "공지 포스트는 챌린지 주최자만 작성할 수 있습니다."),
    ONLY_HOST_CAN_DELETE_ANNOUNCEMENT(HttpStatus.FORBIDDEN, "공지 포스트는 챌린지 호스트만 삭제할 수 있습니다."),
    POST_CANNOT_BE_DELETED(HttpStatus.FORBIDDEN, "일반 포스트 삭제는 제공되지 않는 기능입니다."),
    POST_PHOTO_IMAGE_SIZE_TOO_LARGE(HttpStatus.BAD_REQUEST, "이미지 파일의 크기가 제한을 초과했습니다. (최대 1MB)"),
    PHOTO_NOT_FOUND(HttpStatus.NOT_FOUND, "포스트 포토가 존재하지 않습니다."),
    POST_EDITABLE_ONLY_WITHIN_CHALLENGE_PERIOD(HttpStatus.BAD_REQUEST, "챌린지 유효 기간에만 포스트를 작성 및 수정할 수 있습니다.");

    private HttpStatus status;
    private final String message;

}
