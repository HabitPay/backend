package com.habitpay.habitpay.global.config.auth;

import com.habitpay.habitpay.domain.member.application.MemberService;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.refreshToken.application.NewTokenService;
import com.habitpay.habitpay.domain.refreshToken.application.RefreshTokenService;
import com.habitpay.habitpay.domain.refreshToken.dao.RefreshTokenRepository;
import com.habitpay.habitpay.domain.refreshToken.domain.RefreshToken;
import com.habitpay.habitpay.global.config.jwt.TokenProvider;
import com.habitpay.habitpay.global.config.jwt.TokenService;
import com.habitpay.habitpay.global.util.CookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@AllArgsConstructor
@Component
public class CustomOAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final MemberService memberService;
    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        if (authentication.getPrincipal() instanceof OAuth2User oAuth2User) {
            String email = oAuth2User.getAttribute("email");

            // todo : remove
//            refreshTokenService.setRefreshTokenByEmail(request, response, email);

            String accessToken = tokenService.createAccessToken(email);
            String redirectUrl = "http://localhost:3000/onboarding?accessToken=" + accessToken;
            super.clearAuthenticationAttributes(request);
            response.sendRedirect(redirectUrl);

            // todo : for test
            System.out.println(accessToken);
        }
    }

}
