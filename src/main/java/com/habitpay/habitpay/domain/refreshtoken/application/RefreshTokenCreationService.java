package com.habitpay.habitpay.domain.refreshtoken.application;

import com.habitpay.habitpay.domain.member.application.MemberService;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.refreshtoken.dao.RefreshTokenRepository;
import com.habitpay.habitpay.domain.refreshtoken.domain.RefreshToken;
import com.habitpay.habitpay.domain.refreshtoken.dto.CreateAccessTokenRequest;
import com.habitpay.habitpay.domain.refreshtoken.dto.CreateAccessTokenResponse;
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
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class RefreshTokenCreationService {

    private final TokenProvider tokenProvider;

    private final MemberService memberService;
    private final TokenService tokenService;
    private final RefreshTokenUtilService refreshTokenUtilService;
    private final RefreshTokenSearchService refreshTokenSearchService;

    private final RefreshTokenRepository refreshTokenRepository;


    public CreateAccessTokenResponse createNewAccessTokenAndNewRefreshToken(CreateAccessTokenRequest requestBody) {

        Optional<String> optionalGrantType = Optional.ofNullable(requestBody.getGrantType());
        if (optionalGrantType.isEmpty() || !requestBody.getGrantType().equals("refresh_token")) {
            throw new CustomJwtException(HttpStatus.BAD_REQUEST, CustomJwtErrorInfo.BAD_REQUEST, "Request was missing the 'grantType' parameter.");
        }

        String newAccessToken = this.createNewAccessToken(requestBody.getRefreshToken());
        String refreshToken = this.setRefreshTokenByEmail(tokenService.getEmail(newAccessToken));

        return new CreateAccessTokenResponse(
                        newAccessToken,
                        "Bearer",
                        tokenService.getAccessTokenExpiresInToMillis(),
                        refreshToken);
    }

    private String createNewAccessToken(String refreshToken) {

        tokenProvider.validateToken(refreshToken);

        String requestIp = refreshTokenUtilService.getClientIpAddress();
        String loginIp = refreshTokenSearchService.findByRefreshToken(refreshToken).getLoginIp();
        log.info("[Client new request IP] {}", requestIp);
        log.info("[Client old login IP] {}", loginIp);
        if (!Objects.equals(requestIp, loginIp)) {
            throw new CustomJwtException(HttpStatus.BAD_REQUEST, CustomJwtErrorInfo.BAD_REQUEST, "request IP address is different from login IP address.");
        }

        Long memberId = refreshTokenSearchService.findByRefreshToken(refreshToken).getMember().getId();
        Member member = memberService.findById(memberId);

        // todo : 토큰 유효 기간
        return tokenProvider.generateToken(member, Duration.ofHours(2));
    }

    private void saveRefreshToken(Member member, String newRefreshToken) {
        String loginId = refreshTokenUtilService.getClientIpAddress();
        RefreshToken refreshToken = refreshTokenRepository.findByMemberId(member.getId())
                .map(entity -> entity.update(newRefreshToken, loginId))
                .orElse(new RefreshToken(member, newRefreshToken, loginId));

        refreshTokenRepository.save(refreshToken);
    }

    public String setRefreshTokenByEmail(String email) {
        Member member = memberService.findByEmail(email);

        String refreshToken = tokenService.createRefreshToken(email);
        saveRefreshToken(member, refreshToken);

        return refreshToken;
    }

}
