package com.habitpay.habitpay.domain.challengePost.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ModifyPostRequest {
    private String content;
    private boolean isAnnouncement;
}
