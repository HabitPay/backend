package com.habitpay.habitpay.domain.member.application;

import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MemberProfileService {
    public boolean isNicknameValidFormat(String nickname) {
        String regex = "^[a-zA-Z0-9가-힣]{2,15}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(nickname);

        return matcher.matches();
    }

}
