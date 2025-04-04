package com.habitpay.habitpay.global.config.auth;

import com.habitpay.habitpay.domain.refreshtoken.application.RefreshTokenCreationService;
import com.habitpay.habitpay.global.config.jwt.TokenService;
import com.habitpay.habitpay.global.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${oauth.redirect-url}")
    private String redirectUrl;

    private final TokenService tokenService;
    private final RefreshTokenCreationService refreshTokenCreationService;
    private final CookieUtil cookieUtil;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        if (authentication.getPrincipal() instanceof OAuth2User oAuth2User) {
            log.info("principal: {}", authentication.getPrincipal());
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            Long memberId = customUserDetails.getId();
            String accessToken = tokenService.createAccessToken(memberId);
            String refreshToken = refreshTokenCreationService.createRefreshToken(memberId);

            super.clearAuthenticationAttributes(request);

            cookieUtil.setRefreshToken(response, refreshToken);

            response.sendRedirect(UriComponentsBuilder.fromUriString(redirectUrl)
                    .queryParam("accessToken", accessToken)
                    .build().toUriString());
        }
    }

}
