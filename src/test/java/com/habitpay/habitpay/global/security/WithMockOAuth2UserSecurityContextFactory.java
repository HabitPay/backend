package com.habitpay.habitpay.global.security;

import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.member.domain.Role;
import com.habitpay.habitpay.global.config.auth.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockOAuth2UserSecurityContextFactory implements WithSecurityContextFactory<WithMockOAuth2User> {

    @Override
    public SecurityContext createSecurityContext(WithMockOAuth2User annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        CustomUserDetails principal = new CustomUserDetails(Member.builder()
                .id(1L)
                .email("test@habit.pay")
                .imageFileName("testImageFileName")
                .nickname("testNickname")
                .role(Role.USER)
                .build());

        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "password", principal.getAuthorities());
        context.setAuthentication(authentication);
        return context;
    }

}
