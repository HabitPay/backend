package com.habitpay.habitpay.global.config.auth.interceptor;

import com.habitpay.habitpay.domain.member.application.MemberService;
import com.habitpay.habitpay.domain.member.domain.Member;
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
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.StringTokenizer;

@Component
@RequiredArgsConstructor
@Slf4j
public class SignUpInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;
    private final TokenProvider tokenProvider;
    private final MemberService memberService;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        String REDIRECT_URL = "http://localhost:3000";

        // todo
//        if (!(handler instanceof HandlerMethod)) {}

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        // todo
        log.info("Bean : {}", handlerMethod.getBean());
        log.info("Method : {}", method);

        String authorizationHeader = request.getHeader("Authorization");

        // todo : if 분기 말고 더 좋은 예외 처리 문법?
        if (authorizationHeader == null) {

            // todo
//            response.sendRedirect(REDIRECT_URL);
//            return false;

            throw new IllegalAccessException("authorization Header is null");
        }

        StringTokenizer tokenizer = new StringTokenizer(authorizationHeader);
        if (tokenizer.countTokens() == 2 && tokenizer.nextToken().equals("Bearer")) {
            String token = tokenizer.nextToken();

            // todo
            log.info("Interceptor token (before validation) : {}", token);

            if (!tokenProvider.validateToken(token)) {
                // todo
//            response.sendRedirect(REDIRECT_URL);
//            return false;

                throw new IllegalAccessException("not valid token");
            }

            Authentication authentication = tokenService.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // todo
            log.info("Interceptor authorization success : {}", authentication);

            // todo
            String email = authentication.getName();
            Member member = memberService.findByEmail(email);
            log.info("accessed member isActive : {}", member.isActive());
            log.info("accessed member deletedAt : {}", member.getDeletedAt());
            log.info("accessed member nickname : {}", member.getNickname());
            if (!member.isActive() && member.getDeletedAt() != null) {
                // todo
//            response.sendRedirect(REDIRECT_URL);
//            return false;

                throw new IllegalAccessException("Login, please. You are not a member yet.");
            }

            // todo : 챌린지 페이지로 리다이렉트
//            if (member.isActive()) {
//                response.sendRedirect(REDIRECT_URL + "/challenge");
//                return false;
//            }

            // todo
            Collection<GrantedAuthority> collection = (Collection<GrantedAuthority>) authentication.getAuthorities();
            log.info("Interceptor ROLE check : {}", collection);

            if (!collection.toString().equals("[ROLE_GUEST]")) {
                // todo
//            response.sendRedirect(REDIRECT_URL);
//            return false;

                throw new IllegalAccessException("not permitted ROLE");
            }
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
