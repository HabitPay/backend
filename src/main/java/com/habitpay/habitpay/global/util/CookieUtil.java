package com.habitpay.habitpay.global.util;

import com.habitpay.habitpay.global.error.exception.ErrorCode;
import com.habitpay.habitpay.global.error.exception.UnauthorizedException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;

import java.util.Base64;

import static com.habitpay.habitpay.global.config.jwt.TokenService.REFRESH_TOKEN_EXPIRED_AT;

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

    public String getRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            log.info("cookies is null");
            throw new UnauthorizedException(ErrorCode.JWT_REFRESH_TOKEN_NOT_FOUND);
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                log.info("refreshToken: {}", cookie.getValue());
                return cookie.getValue();
            }
        }

        throw new UnauthorizedException(ErrorCode.JWT_REFRESH_TOKEN_NOT_FOUND);
    }

    public void setRefreshToken(HttpServletResponse response, String refreshToken) {

    ResponseCookie responseCookie = ResponseCookie.from("refresh", refreshToken)
            .httpOnly(true)
            .maxAge(REFRESH_TOKEN_EXPIRED_AT)
            .domain("localhost")
            .path("/")
//            .secure() // todo
            .build();

        response.addHeader("Set-Cookie", responseCookie.toString());
    }
}