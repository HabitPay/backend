package com.habitpay.habitpay.global.config.auth.interceptor;

import com.habitpay.habitpay.global.config.jwt.TokenProvider;
import com.habitpay.habitpay.global.config.jwt.TokenService;
import com.habitpay.habitpay.global.exception.JWT.CustomJwtErrorInfo;
import com.habitpay.habitpay.global.exception.JWT.CustomJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

        // final String REDIRECT_URL = "http://localhost:3000";

        // todo
//        if (!(handler instanceof HandlerMethod)) {}

        // todo : for debug
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        log.info("Bean : {}", handlerMethod.getBean());
        log.info("Method : {}", method);

        String authorizationHeader = request.getHeader("Authorization");
        // todo : authorization header가 빈 값인 경우와, 아예 헤더 자체가 없는 경우 분리 안 되나? 안 되면 에러 메시지 숨겨야 할 듯?
        if (authorizationHeader == null) {
            // todo : 분리 못 하면 그냥 'the request lacks any authentication information' 상태로 보고 처리해도 될 듯
            //      이 경우면, 어떤 error 코드나 정보를 주어선 안 된다고 함! by RFC
            //response.sendRedirect(REDIRECT_URL);
            //return false;
            throw new CustomJwtException(HttpStatus.BAD_REQUEST, CustomJwtErrorInfo.BAD_REQUEST, "Request was missing the 'Authorization' header.");
        }

        StringTokenizer tokenizer = new StringTokenizer(authorizationHeader);
        if (tokenizer.countTokens() == 2 && tokenizer.nextToken().equals("Bearer")) {
            String token = tokenizer.nextToken();

            // todo : for debug
            log.info("[token (before validation)] {}", token);
            if (!tokenProvider.validateToken(token)) {
                // todo : validateToken 메서드 안에서 throw 해서 여기까지 안 옴. 에러 메시지 숨기고 싶을 때 이 코드 사용하기.
                throw new CustomJwtException(HttpStatus.UNAUTHORIZED, CustomJwtErrorInfo.UNAUTHORIZED, "");
            }

            Authentication authentication = tokenService.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // todo : for debug
            log.info("[authorization success] {}", authentication);

            // todo : 인가
            Collection<GrantedAuthority> collection = (Collection<GrantedAuthority>) authentication.getAuthorities();
            log.info("[check ROLE] {}", collection);

//            if (!collection.toString().equals("[ROLE_GUEST]")) {
//                throw new CustomJwtException(HttpStatus.FORBIDDEN, CustomJwtErrorInfo.FORBIDDEN, "request required higher privileges than provided by the
//         access token.");
//            }
            return true;
        }

        // todo
//        response.sendRedirect(REDIRECT_URL);
        return false;
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
