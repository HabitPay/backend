package com.habitpay.habitpay.global.config.jwt;

import com.habitpay.habitpay.global.config.auth.JwtAuthenticationEntryPoint;
import com.habitpay.habitpay.global.util.RequestHeaderUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = RequestHeaderUtil.getAccessToken(request);

        log.info("request: [{} {}]", request.getMethod(), request.getRequestURI());

        try {
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
        } catch (ExpiredJwtException e) {
            System.out.println("만료되었다네요");
            jwtAuthenticationEntryPoint.commence(request, response, new AuthenticationException("만료 토큰!" ,e) {
            });
        } catch (JwtException e) {
            System.out.println("뭔가 JWT 문제");
            jwtAuthenticationEntryPoint.commence(request, response, new AuthenticationException("만료 토큰!" ,e) {
            });
        } catch (Exception e) {
            System.out.println("여기로는 안 오나요,,,,?");
            System.out.println(e.getMessage());

            jwtAuthenticationEntryPoint.commence(request, response, new AuthenticationException("만료 토큰!" ,e) {});
//            request.setAttribute("exception", e);
        }

//        filterChain.doFilter(request, response);
    }
}
