package com.habitpay.habitpay.domain.refreshToken.application;

import com.habitpay.habitpay.domain.member.application.MemberService;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.global.config.jwt.TokenProvider;
import com.habitpay.habitpay.global.error.CustomJwtErrorInfo;
import com.habitpay.habitpay.domain.refreshToken.exception.CustomJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;

@RequiredArgsConstructor
@Service
@Slf4j
public class NewTokenService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final MemberService memberService;

    public String createNewAccessToken(String refreshToken) {

        tokenProvider.validateToken(refreshToken);

        String requestIp = refreshTokenService.getClientIpAddress();
        String loginIp = refreshTokenService.findByRefreshToken(refreshToken).getLoginIp();
        log.info("[Client new request IP] {}", requestIp);
        log.info("[Client old login IP] {}", loginIp);
        if (!Objects.equals(requestIp, loginIp)) {
            throw new CustomJwtException(HttpStatus.BAD_REQUEST, CustomJwtErrorInfo.BAD_REQUEST, "request IP address is different from login IP address.");
        }

        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        Member member = memberService.findById(userId);

        // todo : 토큰 유효 기간
        return tokenProvider.generateToken(member, Duration.ofHours(2));
    }
}
