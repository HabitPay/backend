package com.habitpay.habitpay.domain.member.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ImageUpdateRequest {
    private String extension;
    private Long contentLength;
}
