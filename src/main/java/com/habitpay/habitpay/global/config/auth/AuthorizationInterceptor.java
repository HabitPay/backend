package com.habitpay.habitpay.global.config.auth;

import com.habitpay.habitpay.global.config.jwt.TokenProvider;
import com.habitpay.habitpay.global.config.jwt.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationObservationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

// todo 인터셉터에서 인증하지 않는 경우 (필터에서 인증한 authentication값 확인 가능)
//@Component
//@RequiredArgsConstructor
//public class AuthorizationInterceptor implements HandlerInterceptor {
//
//    @Override
//    public boolean preHandle(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            Object handler) throws Exception {
//
//        HandlerMethod handlerMethod = (HandlerMethod) handler;
//        Method method = handlerMethod.getMethod();
//
//        System.out.println("Bean : " + handlerMethod.getBean());
//        System.out.println("Method : " + method);
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        System.out.println("Interceptor authentication : " + authentication);
//
//        return true;
//    }
//}

//todo 인터셉터가 인증하는 경우
@Component
@RequiredArgsConstructor
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

        System.out.println("Bean : " + handlerMethod.getBean());
        System.out.println("Method : " + method);

        //todo 인증 코드
        String authorizationHeader = request.getHeader("Authorization");

        // todo header가 비어 있을 경우
        if (authorizationHeader == null) {
            throw new IllegalAccessException("authorization 헤더 : null");
        }

        StringTokenizer tokenizer = new StringTokenizer(authorizationHeader);
        if (tokenizer.countTokens() == 2 && tokenizer.nextToken().equals("Bearer")) {
            String token = tokenizer.nextToken();

            System.out.println("Interceptor token(before validation) : " + token);

            if (tokenProvider.validateToken(token)) {
                Authentication authentication = tokenService.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                System.out.println("Interceptor authentication success : " + authentication);
                // isActive, ExpitedAt 등 확인하기
            }
            // todo 여기까지

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            System.out.println("Interceptor authentication : " + authentication);
            Collection<GrantedAuthority> collection = (Collection<GrantedAuthority>) authentication.getAuthorities();
            System.out.println("Interceptor ROLE check : " + collection);

            if (!collection.toString().equals("[ROLE_GUEST]")) {
                throw new IllegalAccessException("authorization 헤더 : 유효하지 않은 JWT");
            }
        }
            return true;
    }
}