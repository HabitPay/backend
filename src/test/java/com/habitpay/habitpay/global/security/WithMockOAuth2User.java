package com.habitpay.habitpay.global.security;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockOAuth2UserSecurityContextFactory.class)
public @interface WithMockOAuth2User {
    String name() default "testName";

    String email() default "test@test.com";


    String role() default "ROLE_USER";
}
