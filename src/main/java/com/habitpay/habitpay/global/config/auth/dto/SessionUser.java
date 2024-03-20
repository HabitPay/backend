package com.habitpay.habitpay.global.config.auth.dto;

import com.habitpay.habitpay.domain.member.domain.Member;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {
    private String email;

    public SessionUser(Member member) {
        this.email = member.getEmail();
    }
}
