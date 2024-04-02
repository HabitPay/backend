package com.habitpay.habitpay.global.config.auth;

import com.habitpay.habitpay.global.config.jwt.TokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@AllArgsConstructor
@Component
public class CustomOAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final TokenService tokenService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        if (authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String email = oAuth2User.getAttribute("email");
            String accessToken = tokenService.createAccessToken(email);
            String redirectUrl = "http://localhost:3000/onboarding?accessToken=" + accessToken;

            response.sendRedirect(redirectUrl);

            // todo : for test
            System.out.println("token : " + accessToken);
        }
    }
}
