package com.habitpay.habitpay.domain.refreshtoken.application;

import com.habitpay.habitpay.domain.member.application.MemberSearchService;
import com.habitpay.habitpay.domain.member.dao.MemberRepository;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.refreshtoken.dao.RefreshTokenRepository;
import com.habitpay.habitpay.domain.refreshtoken.domain.RefreshToken;
import com.habitpay.habitpay.domain.refreshtoken.dto.CreateAccessTokenRequest;
import com.habitpay.habitpay.domain.refreshtoken.dto.CreateAccessTokenResponse;
import com.habitpay.habitpay.domain.refreshtoken.exception.CustomJwtException;
import com.habitpay.habitpay.global.config.jwt.TokenProvider;
import com.habitpay.habitpay.global.config.jwt.TokenService;
import com.habitpay.habitpay.global.error.CustomJwtErrorInfo;
import com.habitpay.habitpay.global.response.SuccessResponse;
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

    private final TokenProvider tokenProvider;

    private final MemberSearchService memberSearchService;
    private final MemberRepository memberRepository;
    private final TokenService tokenService;
    private final RefreshTokenUtilService refreshTokenUtilService;
    private final RefreshTokenSearchService refreshTokenSearchService;

    private final RefreshTokenRepository refreshTokenRepository;


    public SuccessResponse<CreateAccessTokenResponse> createNewAccessTokenAndNewRefreshToken(CreateAccessTokenRequest requestBody) {

        String grantType = requestBody.getGrantType();

        if (grantType == null) {
            throw new CustomJwtException(HttpStatus.BAD_REQUEST, CustomJwtErrorInfo.BAD_REQUEST, "인증 방법을 알 수 없습니다.");
        }

        if (!grantType.equals("refreshToken")) {
            throw new CustomJwtException(HttpStatus.BAD_REQUEST, CustomJwtErrorInfo.BAD_REQUEST, "취급할 수 없는 인증 방법입니다.");
        }

        String newAccessToken = this.createNewAccessToken(requestBody.getRefreshToken());
        String refreshToken = this.createRefreshToken(tokenService.getUserId(newAccessToken));

        CreateAccessTokenResponse tokenResponse = new CreateAccessTokenResponse(
                newAccessToken,
                "Bearer",
                tokenService.getAccessTokenExpiresInToMillis(),
                refreshToken);

        return SuccessResponse.of(
                "새로운 액세스 토큰 및 리프레시 토큰이 성공적으로 발급되었습니다.",
                tokenResponse
        );
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
        Member member = memberSearchService.getMemberById(memberId);

        // todo : 토큰 유효 기간
        return tokenProvider.generateToken(member, Duration.ofHours(2));
    }

    public String createRefreshToken(Long id) {
        Member member = memberSearchService.getMemberById(id);
        String refreshToken = tokenService.createRefreshTokenContent(id);
        saveRefreshToken(member, refreshToken);

        return refreshToken;
    }

    private void saveRefreshToken(Member member, String newRefreshToken) {
        String loginId = refreshTokenUtilService.getClientIpAddress();
        RefreshToken refreshToken = refreshTokenRepository.findByMemberId(member.getId())
                .map(entity -> entity.update(newRefreshToken, loginId))
                .orElse(new RefreshToken(member, newRefreshToken, loginId));

        refreshTokenRepository.save(refreshToken);
    }

}
