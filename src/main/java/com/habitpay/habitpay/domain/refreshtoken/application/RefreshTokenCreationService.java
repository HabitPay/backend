package com.habitpay.habitpay.domain.refreshtoken.application;

import com.habitpay.habitpay.domain.member.application.MemberService;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.refreshtoken.dao.RefreshTokenRepository;
import com.habitpay.habitpay.domain.refreshtoken.domain.RefreshToken;
import com.habitpay.habitpay.domain.refreshtoken.exception.CustomJwtException;
import com.habitpay.habitpay.global.config.jwt.TokenProvider;
import com.habitpay.habitpay.global.config.jwt.TokenService;
import com.habitpay.habitpay.global.error.CustomJwtErrorInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;

@RequiredArgsConstructor
@Service
@Slf4j
public class RefreshTokenCreationService {

    private final MemberService memberService;
    private final TokenService tokenService;
    private final RefreshTokenUtilService refreshTokenUtilService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;
    private final RefreshTokenSearchService refreshTokenSearchService;

    public String createNewAccessToken(String refreshToken) {

        tokenProvider.validateToken(refreshToken);

        String requestIp = refreshTokenUtilService.getClientIpAddress();
        String loginIp = refreshTokenSearchService.findByRefreshToken(refreshToken).getLoginIp();
        log.info("[Client new request IP] {}", requestIp);
        log.info("[Client old login IP] {}", loginIp);
        if (!Objects.equals(requestIp, loginIp)) {
            throw new CustomJwtException(HttpStatus.BAD_REQUEST, CustomJwtErrorInfo.BAD_REQUEST, "request IP address is different from login IP address.");
        }

        Long userId = refreshTokenSearchService.findByRefreshToken(refreshToken).getUserId();
        Member member = memberService.findById(userId);

        // todo : 토큰 유효 기간
        return tokenProvider.generateToken(member, Duration.ofHours(2));
    }

    public void saveRefreshToken(Long userId, String newRefreshToken) {
        String loginId = refreshTokenUtilService.getClientIpAddress();
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(newRefreshToken, loginId))
                .orElse(new RefreshToken(userId, newRefreshToken, loginId));

        refreshTokenRepository.save(refreshToken);
    }

    public String setRefreshTokenByEmail(String email) {
        Member member = memberService.findByEmail(email);

        String refreshToken = tokenService.createRefreshToken(email);
        saveRefreshToken(member.getId(), refreshToken);

        //todo : for test
        System.out.println("refresh token : " + refreshToken);

        return refreshToken;
    }

}
