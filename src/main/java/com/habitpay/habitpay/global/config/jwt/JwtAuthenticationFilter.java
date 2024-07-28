package com.habitpay.habitpay.global.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habitpay.habitpay.domain.refreshtoken.exception.CustomJwtException;
import com.habitpay.habitpay.global.config.auth.JwtAuthenticationEntryPoint;
import com.habitpay.habitpay.global.error.CustomJwtErrorInfo;
import com.habitpay.habitpay.global.error.ErrorResponse;
import com.habitpay.habitpay.global.error.exception.ErrorCode;
import com.habitpay.habitpay.global.util.RequestHeaderUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = RequestHeaderUtil.getAccessToken(request);

        log.info("request: [{} {}]", request.getMethod(), request.getRequestURI());

        // TODO: 예외처리 추가하기

        if (accessToken == null) {
            log.error("액세스 토큰이 존재하지 않습니다.");
        } else if (tokenProvider.validateToken(accessToken)) {
            Authentication authentication = tokenService.getAuthentication(accessToken);
            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(authentication);
            log.info("authentication.getName(): {}", authentication.getName());
        }

        filterChain.doFilter(request, response);
    }
}
