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
import com.habitpay.habitpay.global.error.exception.BadRequestException;
import com.habitpay.habitpay.global.error.exception.ErrorCode;
import com.habitpay.habitpay.global.error.exception.UnauthorizedException;
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
    private final TokenService tokenService;
    private final RefreshTokenUtilService refreshTokenUtilService;
    private final RefreshTokenSearchService refreshTokenSearchService;

    private final RefreshTokenRepository refreshTokenRepository;


    public SuccessResponse<CreateAccessTokenResponse> createNewAccessTokenAndNewRefreshToken(CreateAccessTokenRequest requestBody) {

        String grantType = requestBody.getGrantType();

        if (grantType == null) {
            log.error("요청 헤더 grantType의 값이 null입니다.");
            throw new BadRequestException(ErrorCode.JWT_GRANT_TYPE_IS_BAD);
        }

        if (!grantType.equals("refreshToken")) {
            log.error("요청 헤더 grantType의 값이 refreshToken이 아닙니다.");
            throw new BadRequestException(ErrorCode.JWT_GRANT_TYPE_IS_BAD);
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

        if (!tokenProvider.validateToken(refreshToken)) {
            throw new UnauthorizedException("리프레시 토큰 검증에 실패했습니다.", ErrorCode.JWT_UNAUTHORIZED);
        }

        String requestIp = refreshTokenUtilService.getClientIpAddress();
        String loginIp = refreshTokenSearchService.findByRefreshToken(refreshToken).getLoginIp();
        log.info("[Client request IP] {}, [Client login IP] {}", requestIp, loginIp);
        if (!Objects.equals(requestIp, loginIp)) {
            throw new BadRequestException(ErrorCode.JWT_REQUEST_IP_AND_LOGIN_IP_NOT_SAME_FOR_REFRESH);
        }

        Long memberId = refreshTokenSearchService.findByRefreshToken(refreshToken).getMember().getId();
        Member member = memberSearchService.getMemberById(memberId);

        return tokenProvider.generateToken(member, TokenService.ACCESS_TOKEN_EXPIRED_AT);
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
