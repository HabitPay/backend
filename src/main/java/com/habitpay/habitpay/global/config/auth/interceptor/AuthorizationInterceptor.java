package com.habitpay.habitpay.global.config.auth.interceptor;

import com.habitpay.habitpay.domain.member.application.MemberService;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.global.config.jwt.TokenProvider;
import com.habitpay.habitpay.global.config.jwt.TokenService;
import com.habitpay.habitpay.global.exception.CustomJwtErrorResponse;
import com.habitpay.habitpay.global.exception.CustomJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

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

//        String REDIRECT_URL = "http://localhost:3000";

        // todo
//        if (!(handler instanceof HandlerMethod)) {}

        // todo : for debug
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        log.info("Bean : {}", handlerMethod.getBean());
        log.info("Method : {}", method);

        String authorizationHeader = request.getHeader("Authorization");

        // todo : if 분기 말고 더 좋은 예외 처리 문법?
        if (authorizationHeader == null) {
            throw new Exception("authorization Header is null (원인 알 수 없게 일반 에러 메시지로 변경 필요)");
        }

        StringTokenizer tokenizer = new StringTokenizer(authorizationHeader);
        if (tokenizer.countTokens() == 2 && tokenizer.nextToken().equals("Bearer")) {
            String token = tokenizer.nextToken();

            // todo : for debug
            log.info("Interceptor token (before validation) : {}", token);

            if (!tokenProvider.validateToken(token)) {
                throw new CustomJwtException(CustomJwtErrorResponse.UNAUTHORIZED);
//                throw new IllegalAccessException("not valid token");
            }

            Authentication authentication = tokenService.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // todo : for debug
            log.info("Interceptor authorization success : {}", authentication);

            // todo : 인가
            Collection<GrantedAuthority> collection = (Collection<GrantedAuthority>) authentication.getAuthorities();
            log.info("Interceptor ROLE check : {}", collection);

//            if (!collection.toString().equals("[ROLE_GUEST]")) {
//                throw new CustomJwtException(CustomJwtErrorResponse.FORBIDDEN);
//                // throw new IllegalAccessException("not permitted ROLE");
//            }
        }

        // todo
//        response.sendRedirect(REDIRECT_URL);
//        return false;

        return true;
    }

    @Override
    public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView modelAndView) throws Exception {

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        log.info("Interceptor : postHandle()");
        log.info("Method : {}", method);
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception exception) throws Exception {

        log.info("Interceptor : afterCompletion()");
    }

}
