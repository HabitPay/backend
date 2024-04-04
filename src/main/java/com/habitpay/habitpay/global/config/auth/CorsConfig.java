package com.habitpay.habitpay.global.config.auth;

import com.habitpay.habitpay.domain.member.application.MemberService;
import com.habitpay.habitpay.global.config.auth.interceptor.AuthorizationInterceptor;
import com.habitpay.habitpay.global.config.auth.interceptor.SignUpInterceptor;
import com.habitpay.habitpay.global.config.jwt.TokenProvider;
import com.habitpay.habitpay.global.config.jwt.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final TokenService tokenService;
    private final TokenProvider tokenProvider;

    public CorsConfig(TokenService tokenService, TokenProvider tokenProvider, MemberService memberService){
        this.tokenService = tokenService;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("*")
                .allowedOrigins("http://localhost:3000");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(authorizationInterceptor());

        // todo
        registry.addInterceptor(authorizationInterceptor()).addPathPatterns("/api/**");
        registry.addInterceptor(signUpInterceptor()).addPathPatterns("/member");
    }

    @Bean
    public AuthorizationInterceptor authorizationInterceptor() {
        return new AuthorizationInterceptor(tokenService, tokenProvider);
    }

    @Bean
    public SignUpInterceptor signUpInterceptor() {
        return new SignUpInterceptor(tokenService, tokenProvider);
    }
}
