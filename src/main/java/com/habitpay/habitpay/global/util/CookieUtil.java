package com.habitpay.habitpay.global.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CookieUtil {
    public static String getAccessToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            log.info("cookies is null");
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("accessToken")) {
                log.info("accessToken: {}", cookie.getValue());
                return cookie.getValue();
            }
        }

        return null;
    }
}
