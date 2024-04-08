package com.habitpay.habitpay.domain.refreshToken.application;

import com.habitpay.habitpay.domain.member.application.MemberService;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.global.config.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class NewTokenService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final MemberService memberService;

    public String createNewAccessToken(String refreshToken, String requestIp) {

        if (!tokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("새로운 토큰을 발급해주려 했으나 리프레시 토큰이 이상하네요.");
        }

        String loginIp = refreshTokenService.findByRefreshToken(refreshToken).getLoginIp();
        if (!Objects.equals(requestIp, loginIp)) {
            throw new IllegalArgumentException("로그인한 주소와 요청한 주소가 달라요.");
        }

        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        Member member = memberService.findById(userId);

        // todo : 토큰 유효 기간
        return tokenProvider.generateToken(member, Duration.ofHours(2));
    }
}
