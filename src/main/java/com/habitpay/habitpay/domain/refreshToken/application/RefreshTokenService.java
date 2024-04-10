package com.habitpay.habitpay.domain.refreshToken.application;

import com.habitpay.habitpay.domain.member.application.MemberService;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.refreshToken.dao.RefreshTokenRepository;
import com.habitpay.habitpay.domain.refreshToken.domain.RefreshToken;
import com.habitpay.habitpay.global.config.jwt.TokenService;
import com.habitpay.habitpay.global.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private final MemberService memberService;
    private final TokenService tokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);

    // todo: 예외 메시지 수정
    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("이런 refresh token은 없다"));
    }

    public void setRefreshTokenByEmail(HttpServletRequest request, HttpServletResponse response, String email) {
        Member member = memberService.findByEmail(email);

        String refreshToken = tokenService.createRefreshToken(email);
        saveRefreshToken(member.getId(), refreshToken);
        addRefreshTokenToCookie(request, response, refreshToken);

        //todo : for test
        System.out.println("refresh token : " + refreshToken);
    }

    public void saveRefreshToken(Long userId, String newRefreshToken) {
        String loginId = getClientIpAddress();
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(newRefreshToken, loginId))
                .orElse(new RefreshToken(userId, newRefreshToken, loginId));

        refreshTokenRepository.save(refreshToken);
    }

    public void addRefreshTokenToCookie(HttpServletRequest request,
                                         HttpServletResponse response, String refreshToken) {

        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);
    }

    public String getClientIpAddress() {

        if (Objects.isNull(RequestContextHolder.getRequestAttributes())) {
            return "0.0.0.0";
        }

        final String[] IpHeaderCandidates = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "HTTP_VIA",
                "REMOTE_ADDR"
        };

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        for (String header : IpHeaderCandidates) {
            String requestIp = request.getHeader(header);
            if (Objects.nonNull(requestIp) && !requestIp.isEmpty() && !"unknown".equalsIgnoreCase(requestIp)) {
                return requestIp.split(",")[0];
            }
        }
        return request.getRemoteAddr();
    }
}
