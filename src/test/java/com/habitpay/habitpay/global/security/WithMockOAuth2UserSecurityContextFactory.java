package com.habitpay.habitpay.global.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Collections;
import java.util.Map;

public class WithMockOAuth2UserSecurityContextFactory implements WithSecurityContextFactory<WithMockOAuth2User> {

    @Override
    public SecurityContext createSecurityContext(WithMockOAuth2User annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        OAuth2User oAuth2User = new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(annotation.role())),
                Map.of(
                        "name", annotation.name(),
                        "email", annotation.email()
                ),
                "name"
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(oAuth2User, "password", oAuth2User.getAuthorities());
        context.setAuthentication(authentication);
        return context;
    }

}
