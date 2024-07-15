package com.habitpay.habitpay.global.config.jwt;

import com.habitpay.habitpay.global.util.RequestHeaderUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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

        // TODO: 예외처리 추가하기
        if (accessToken == null) {
            log.error("No access token");
        }

        // TODO: 예외처리 밖으로 꺼내오기
        if (tokenProvider.validateToken(accessToken) == false) {
            log.error("Invalid access token");
        }

        Authentication authentication = tokenService.getAuthentication(accessToken);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authentication);
        log.info("authentication.getName(): {}", authentication.getName());

        filterChain.doFilter(request, response);
    }
}
