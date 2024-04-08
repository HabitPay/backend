package com.habitpay.habitpay.domain.refreshToken.application;

import com.habitpay.habitpay.domain.member.application.MemberService;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.global.config.jwt.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
                String ip = requestIp.split(",")[0];
                return ip;
            }
        }
        return request.getRemoteAddr();
    }
}
