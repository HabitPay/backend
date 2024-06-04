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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.Objects;

@RequiredArgsConstructor
@Service
@Slf4j
public class RefreshTokenService {

    private final MemberService memberService;
    private final TokenService tokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new NoSuchElementException("cannot find refresh token in DB"));
    }

    public String setRefreshTokenByEmail(String email) {
        Member member = memberService.findByEmail(email);

        String refreshToken = tokenService.createRefreshToken(email);
        saveRefreshToken(member.getId(), refreshToken);

        //todo : for test
        System.out.println("refresh token : " + refreshToken);

        return refreshToken;
    }

    public void saveRefreshToken(Long userId, String newRefreshToken) {
        String loginId = getClientIpAddress();
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(newRefreshToken, loginId))
                .orElse(new RefreshToken(userId, newRefreshToken, loginId));

        refreshTokenRepository.save(refreshToken);
    }

    public String getClientIpAddress() {

        if (Objects.isNull(RequestContextHolder.getRequestAttributes())) {
            log.info("IP : 0.0.0.0");
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
                log.info("[IP address] {}", requestIp.split(",")[0]);
                return requestIp.split(",")[0];
            }
        }
        String requestIp = request.getRemoteAddr();
        log.info("[IP address] {}", requestIp);
        return requestIp;
    }
}
