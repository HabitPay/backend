package com.habitpay.habitpay.domain.member.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class MemberRequest {
    private String nickname;
    private MultipartFile profileImage;
}
