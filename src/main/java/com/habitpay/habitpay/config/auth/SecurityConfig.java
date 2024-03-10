package com.habitpay.habitpay.config.auth;

import com.habitpay.habitpay.user.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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
                            .requestMatchers("/", "/oauth2/authorization/google").permitAll()
                            .requestMatchers("/api/v1/**").hasRole(Role.USER.name())
                            .anyRequest().authenticated()
            ))
            .logout((logoutConfig) ->
                    logoutConfig.logoutSuccessUrl("/"))
            .oauth2Login((oauth2) ->
                    oauth2.loginPage("/oauth2/authorization/google")
                            .defaultSuccessUrl("http://localhost:3000/success")
                            .failureUrl("http://localhost:3000/fail")
                            .userInfoEndpoint(userInfoEndpoint ->
                                    userInfoEndpoint.userService(customOAuth2UserService)));

        return http.build();
    }


}
