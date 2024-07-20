package com.habitpay.habitpay.global.config.auth;

import com.habitpay.habitpay.global.config.jwt.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

import static com.habitpay.habitpay.global.config.jwt.TokenService.REFRESH_TOKEN_EXPIRED_AT;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${application.origin-url}")
    private String redirectUrl;

    private final TokenService tokenService;

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
            String refreshToken = tokenService.createRefreshToken(memberId);

            super.clearAuthenticationAttributes(request);

            ResponseCookie responseCookie = ResponseCookie.from("refresh", refreshToken)
                    .httpOnly(true)
                    .maxAge(REFRESH_TOKEN_EXPIRED_AT)
                    .domain("localhost")
                    .path("/")
                    .build();

            response.addHeader("Set-Cookie", responseCookie.toString());
            response.sendRedirect(UriComponentsBuilder.fromUriString(redirectUrl)
                    .queryParam("accessToken", accessToken)
                    .build().toUriString());

            // todo : for test
            System.out.println(accessToken);
        }
    }

}
