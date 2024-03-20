package com.habitpay.habitpay.global.config.auth;

import com.habitpay.habitpay.domain.member.domain.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    @Autowired
    private final CustomOAuth2UserService customOAuth2UserService;

    // TODO: CorsConfig.java 파일에 옮길 수 있도록 하기 
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedHeaders(Collections.singletonList("*"));
            config.setAllowedMethods(Collections.singletonList("*"));
            config.setAllowedOriginPatterns(Collections.singletonList("*"));
            config.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
            config.setAllowCredentials(true);
            return config;
        };
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()))
            .csrf((csrf) -> csrf.disable())
            .headers((headerConfig) ->
                    headerConfig.frameOptions(frameOptionsConfig ->
                            frameOptionsConfig.disable()))
            .authorizeHttpRequests((authorizeRequests ->
                    authorizeRequests
                            .requestMatchers("/*").permitAll() // todo: 보안 상 취약할 수 있으니 범위 제한하기
//                            .requestMatchers("/api/v1/**").hasRole(Role.USER.name()) // todo: 로그인 후 사용하는 api 에서만 적용하기
                            .anyRequest().authenticated()
            ))
            .logout((logoutConfig) ->
                    logoutConfig.logoutSuccessUrl("/"))
            .oauth2Login((oauth2) ->
                    oauth2.loginPage("/oauth2/authorization/google")
                            .defaultSuccessUrl("http://localhost:3000/onboarding")
                            .failureUrl("http://localhost:3000/fail")
                            .userInfoEndpoint(userInfoEndpoint ->
                                    userInfoEndpoint.userService(customOAuth2UserService)));

        return http.build();
    }


}
