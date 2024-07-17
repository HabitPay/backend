package com.habitpay.habitpay.domain.member.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ImageUpdateResponse {
    private String preSignedUrl;

    public static ImageUpdateResponse from(String preSignedUrl) {
        return ImageUpdateResponse.builder()
                .preSignedUrl(preSignedUrl)
                .build();
    }
}
