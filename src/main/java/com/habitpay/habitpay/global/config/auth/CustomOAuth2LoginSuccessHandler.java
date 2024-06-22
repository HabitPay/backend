package com.habitpay.habitpay.global.config.auth;

import com.habitpay.habitpay.domain.member.application.MemberService;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.refreshtoken.application.RefreshTokenCreationService;
import com.habitpay.habitpay.global.config.jwt.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@AllArgsConstructor
@Component
@Slf4j
public class CustomOAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final MemberService memberService;
    private final TokenService tokenService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        if (authentication.getPrincipal() instanceof OAuth2User oAuth2User) {
            String redirectUrl = "http://localhost:3000";
            String email = oAuth2User.getAttribute("email");
            Member member = memberService.findByEmail(email);
            String accessToken = tokenService.createAccessToken(email);

            super.clearAuthenticationAttributes(request);
            log.info("[onAuthenticationSuccess] isActive: {}", member.isActive());
            if (member.isActive()) {
                redirectUrl += "/challenges/my_challenge?accessToken=" + accessToken;
            } else {
                redirectUrl += "/onboarding?accessToken=" + accessToken;
            }

            response.sendRedirect(redirectUrl);

            // todo : for test
            System.out.println(accessToken);
        }
    }

}
