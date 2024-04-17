package com.habitpay.habitpay.domain.refreshToken.api;

import com.habitpay.habitpay.domain.member.application.MemberService;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.refreshToken.application.NewTokenService;
import com.habitpay.habitpay.domain.refreshToken.application.RefreshTokenService;
import com.habitpay.habitpay.domain.refreshToken.dto.CreateAccessTokenRequest;
import com.habitpay.habitpay.domain.refreshToken.dto.CreateAccessTokenResponse;
import com.habitpay.habitpay.global.config.jwt.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RequiredArgsConstructor
@RestController
@Slf4j
public class TokenApiController {

    private final TokenService tokenService;
    private final NewTokenService newTokenService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/token")
    public ResponseEntity<CreateAccessTokenResponse> createNewAccessTokenAndNewRefreshToken(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody CreateAccessTokenRequest requestBody) {

        //todo : refresh token을 요청 헤더로 받을 경우
//        if (request.getHeader("grant_type").equals("refresh_token")) {
//            String newAccessToken = newTokenService.createNewAccessToken(request.getHeader("refresh_token"));
//        }
        // todo: refresh token을 요청 바디로 받을 경우
        String newAccessToken = newTokenService.createNewAccessToken(requestBody.getRefreshToken());

        String email = tokenService.getEmail(newAccessToken);
        refreshTokenService.setRefreshTokenByEmail(response, email);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateAccessTokenResponse(newAccessToken));
    }
}
