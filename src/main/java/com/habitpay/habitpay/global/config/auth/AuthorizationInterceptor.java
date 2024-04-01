package com.habitpay.habitpay.global.config.auth;

import com.habitpay.habitpay.global.config.jwt.TokenProvider;
import com.habitpay.habitpay.global.config.jwt.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.StringTokenizer;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorizationInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;
    private final TokenProvider tokenProvider;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        // todo
        log.info("Bean : {}", handlerMethod.getBean());
        log.info("Method : {}", method);

        String authorizationHeader = request.getHeader("Authorization");

        // todo : if 분기 말고 더 좋은 예외 처리 문법?
        if (authorizationHeader == null) {
            throw new IllegalAccessException("authorization Header is null");
        }

        StringTokenizer tokenizer = new StringTokenizer(authorizationHeader);
        if (tokenizer.countTokens() == 2 && tokenizer.nextToken().equals("Bearer")) {
            String token = tokenizer.nextToken();

            // todo
            log.info("Interceptor token (before validation) : {}", token);

            if (!tokenProvider.validateToken(token)) {
                throw new IllegalAccessException("not valid token");
            }

            Authentication authentication = tokenService.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // todo
            log.info("Interceptor authorization success : {}", authentication);

            // todo : authentication 바탕으로 유저 정보 불러와서 isActive, DeletedAt 확인

            // todo
            Collection<GrantedAuthority> collection = (Collection<GrantedAuthority>) authentication.getAuthorities();
            log.info("Interceptor ROLE check : {}", collection);

            if (!collection.toString().equals("[ROLE_GUEST]")) {
                throw new IllegalAccessException("not permitted ROLE");
            }
        }

        // todo : 얘도 throw 해버리기?
        return false;
    }
}
