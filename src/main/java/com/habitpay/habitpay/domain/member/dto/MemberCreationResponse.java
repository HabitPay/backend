package com.habitpay.habitpay.domain.member.dto;

import com.habitpay.habitpay.global.response.BaseResponse;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberCreationResponse extends BaseResponse {
    private String nickname;
}
