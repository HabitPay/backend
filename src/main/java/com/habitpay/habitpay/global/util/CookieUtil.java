package com.habitpay.habitpay.global.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;

import java.util.Base64;

// todo : refresh token 목적으로 만든 util. 추후 필요 없으면 삭제하기
@Service
@Slf4j
public class CookieUtil {

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
//        cookie.setSecure(true);
        response.addCookie(cookie);
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return;
        }

        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
    }

    // todo : 보안에 취약. 직렬화 및 역직렬화 방식 바꿔야 함.
//    public static String serialize(Object object) {
//        return Base64.getUrlEncoder()
//                .encodeToString(SerializationUtils.serialize(object));
//    }
//
//    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
//        return cls.cast(
//                SerializationUtils.deserialize(
//                        Base64.getUrlDecoder().decode(cookie.getValue())
//                )
//        );
//    }

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

    public String getRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            log.info("cookies is null");
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                log.info("refreshToken: {}", cookie.getValue());
                return cookie.getValue();
            }
        }

        return null;
    }
}
