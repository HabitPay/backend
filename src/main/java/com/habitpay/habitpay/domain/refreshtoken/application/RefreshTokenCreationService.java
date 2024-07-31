package com.habitpay.habitpay.domain.refreshtoken.application;

import com.habitpay.habitpay.domain.member.application.MemberSearchService;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.refreshtoken.dao.RefreshTokenRepository;
import com.habitpay.habitpay.domain.refreshtoken.domain.RefreshToken;
import com.habitpay.habitpay.domain.refreshtoken.dto.CreateAccessTokenRequest;
import com.habitpay.habitpay.domain.refreshtoken.dto.CreateAccessTokenResponse;
import com.habitpay.habitpay.global.config.jwt.TokenProvider;
import com.habitpay.habitpay.global.config.jwt.TokenService;
import com.habitpay.habitpay.global.error.exception.BadRequestException;
import com.habitpay.habitpay.global.error.exception.ErrorCode;
import com.habitpay.habitpay.global.error.exception.UnauthorizedException;
import com.habitpay.habitpay.global.response.SuccessCode;
import com.habitpay.habitpay.global.response.SuccessResponse;
import com.habitpay.habitpay.global.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.habitpay.habitpay.global.config.jwt.TokenService.REFRESH_TOKEN_EXPIRED_AT;

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

    // todo
    private final CookieUtil cookieUtil;


    public SuccessResponse<CreateAccessTokenResponse> createNewAccessTokenAndNewRefreshToken(
            HttpServletRequest request,
            HttpServletResponse response) {

//        String grantType = requestBody.getGrantType();
//
//        if (grantType == null) {
//            log.error("요청 헤더 grantType의 값이 null입니다.");
//            throw new BadRequestException(ErrorCode.BAD_REQUEST);
//        }
//
//        if (!grantType.equals("refreshToken")) {
//            log.error("요청 헤더 grantType의 값이 refreshToken이 아닙니다.");
//            throw new BadRequestException(ErrorCode.BAD_REQUEST);
//        }
//
//        String newAccessToken = this.createNewAccessToken(requestBody.getRefreshToken());

        String refreshTokenFromCookie = cookieUtil.getRefreshToken(request);
        if (refreshTokenFromCookie == null) {
            throw new UnauthorizedException(ErrorCode.JWT_REFRESH_TOKEN_NOT_FOUND);
        }

        String newAccessToken = this.createNewAccessToken(refreshTokenFromCookie);
        String refreshToken = this.createRefreshToken(tokenService.getUserId(newAccessToken));

        CreateAccessTokenResponse tokenResponse = new CreateAccessTokenResponse(
                newAccessToken,
                "Bearer",
                tokenService.getAccessTokenExpiresInToMillis(),
                refreshToken);

        // todo
        ResponseCookie responseCookie = ResponseCookie.from("refresh", refreshToken)
                .httpOnly(true)
                .maxAge(REFRESH_TOKEN_EXPIRED_AT)
                .domain("localhost")
                .path("/")
                .build();

        response.addHeader("Set-Cookie", responseCookie.toString());


        return SuccessResponse.of(
                SuccessCode.REFRESH_TOKEN_SUCCESS,
                tokenResponse
        );
    }

    private String createNewAccessToken(String refreshToken) {

        if (!tokenProvider.validateToken(refreshToken)) {
            log.error("리프레시 토큰 인증에 실패했습니다.");
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED);
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
        RefreshToken refreshToken = refreshTokenRepository.findByMember(member)
                .map(entity -> entity.update(newRefreshToken, loginId))
                .orElse(new RefreshToken(member, newRefreshToken, loginId));

        refreshTokenRepository.save(refreshToken);
    }

}
