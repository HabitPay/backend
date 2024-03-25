package com.habitpay.habitpay.global.config.auth;

import com.habitpay.habitpay.global.config.jwt.TokenProvider;
import com.habitpay.habitpay.global.config.jwt.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationObservationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
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
        StringTokenizer tokenizer = new StringTokenizer(authorizationHeader);
        if (tokenizer.countTokens() == 2 && tokenizer.nextToken().equals("Bearer")) {
            String token = tokenizer.nextToken();

            System.out.println("Interceptor token(before validation) : " + token);

            if (tokenProvider.validateToken(token)) {
                Authentication authentication = tokenService.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                System.out.println("authentication success : " + authentication);
            }
            // todo 여기까지

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("Interceptor authentication : " + authentication);

            Collection<GrantedAuthority> collection = (Collection<GrantedAuthority>) authentication.getAuthorities();
            System.out.println(collection);

            boolean ok = collection.toString().equals("[ROLE_GUEST]");
            if (!ok) {
                response.sendRedirect("localhost:3000/error");
                return ok;
            }
            return true;

//            System.out.println("인증 내용 :" + authentication.getAuthorities().toString());
//            return authentication.getAuthorities().toString().equals("ROLE_GUEST");
        }
            return true;
    }
}