package com.habitpay.habitpay.global.config.auth;

import com.habitpay.habitpay.global.config.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOAuth2LoginSuccessHandler customOAuth2LoginSuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    // TODO: CorsConfig.java 파일에 옮기기
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedHeaders(Collections.singletonList("*"));
            config.setAllowedMethods(Collections.singletonList("*"));
            config.setAllowedOriginPatterns(Collections.singletonList("*"));
            config.setAllowedOrigins(Collections.singletonList(allowedOrigins));
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
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/oauth2/**").permitAll()
                                .requestMatchers("/api/token").permitAll()
                                .requestMatchers("/actuator/health").permitAll()
//                            .requestMatchers("/api/v1/**").hasRole(Role.USER.name()) // todo: 로그인 후 사용하는 api 에서만 적용하기
                                .anyRequest().authenticated()
                ))
                .logout((logoutConfig) ->
                        logoutConfig.logoutSuccessUrl("/"))
                .oauth2Login((oauth2) ->
                        oauth2.loginPage("/oauth2/authorization/google")
                                .successHandler(customOAuth2LoginSuccessHandler)
                                .failureUrl(allowedOrigins+ "/fail") // TODO: failureHandler 추가하기
                                .userInfoEndpoint(userInfoEndpoint ->
                                        userInfoEndpoint.userService(customOAuth2UserService)));

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling((handler) ->
                handler.authenticationEntryPoint(jwtAuthenticationEntryPoint));

        return http.build();
    }


}
