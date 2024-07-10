package com.habitpay.habitpay.global.config.auth.interceptor;

import com.habitpay.habitpay.domain.refreshtoken.exception.CustomJwtException;
import com.habitpay.habitpay.global.config.jwt.TokenProvider;
import com.habitpay.habitpay.global.config.jwt.TokenService;
import com.habitpay.habitpay.global.error.CustomJwtErrorInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
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

        if (!(handler instanceof HandlerMethod)) {
            // todo : response로 error 응답 보내기 or throw
            log.error("handler is not instanceof HandlerMethod"); // 임시
            return false;
        }

        // todo : 디버깅 목적
        printDebugLine(handler);

        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null) {
            // return false;
            // todo : 예외 정보를 숨기는 방향으로 에러 메시지 수정
            throw new CustomJwtException(HttpStatus.BAD_REQUEST, CustomJwtErrorInfo.BAD_REQUEST, "Request was missing the 'Authorization' header.");
        }

        StringTokenizer tokenizer = new StringTokenizer(authorizationHeader);
        if (tokenizer.countTokens() == 2 && tokenizer.nextToken().equals("Bearer")) {

            String token = tokenizer.nextToken();
            log.info(token);
            tokenProvider.validateToken(token);

            boolean isSignupRequest = getIsSignupRequest(request, token);
            if (isSignupRequest) { return true; }

            if (!tokenService.getIsActive(token)) {
                throw new CustomJwtException(HttpStatus.UNAUTHORIZED, CustomJwtErrorInfo.UNAUTHORIZED, "Not an available token.");
            }

            Authentication authentication = tokenService.getAuthentication(token);
            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(authentication);

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
        // todo : response로 error 응답 보내기 or throw
        log.error("might not be Bearer token"); // 임시
        return false;
    }

    // todo : 나중에 삭제 예정
    private void printDebugLine(Object handler) {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        log.info("Bean : {}", handlerMethod.getBean());
        log.info("Method : {}", method);
    }

    private boolean getIsSignupRequest(HttpServletRequest request, String token) {
        String httpMethod = request.getMethod();
        String requestURI = request.getRequestURI();

        boolean isPostMethod = "POST".equalsIgnoreCase(httpMethod);
        boolean isMemberApi = "/api/member".equals(requestURI);

        if (isMemberApi && isPostMethod) {
            if (!tokenService.getIsActive(token)) {
                return true;
            } else {
                throw new CustomJwtException(HttpStatus.UNAUTHORIZED, CustomJwtErrorInfo.UNAUTHORIZED, "Signup try with a member already signed up.");
            }
        }
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

        log.info("Method : {}", method);
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception exception) throws Exception {

        // todo : 삭제 예정
        log.info("Interceptor : afterCompletion()");

        if (exception != null) {
            log.error(exception.getMessage());
        }
    }

}