package com.habitpay.habitpay.global.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;

public class RequestHeaderUtil {

    private static final String TOKEN_PREFIX = "Bearer";


    public static String getAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (bearerToken == null) {
            return null;
        }

        if (bearerToken.startsWith(TOKEN_PREFIX) == false) {
            return null;
        }

        return bearerToken.substring(TOKEN_PREFIX.length());
    }
}
