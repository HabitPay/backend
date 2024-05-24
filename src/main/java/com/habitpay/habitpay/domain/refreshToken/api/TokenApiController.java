package com.habitpay.habitpay.domain.refreshToken.api;

import com.habitpay.habitpay.domain.refreshToken.application.NewTokenService;
import com.habitpay.habitpay.domain.refreshToken.application.RefreshTokenService;
import com.habitpay.habitpay.domain.refreshToken.dto.CreateAccessTokenRequest;
import com.habitpay.habitpay.domain.refreshToken.dto.CreateAccessTokenResponse;
import com.habitpay.habitpay.global.config.jwt.TokenService;
import com.habitpay.habitpay.global.error.CustomJwtErrorInfo;
import com.habitpay.habitpay.domain.refreshToken.exception.CustomJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
@Slf4j
public class TokenApiController {

    private final TokenService tokenService;
    private final NewTokenService newTokenService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/token")
    public ResponseEntity<CreateAccessTokenResponse> createNewAccessTokenAndNewRefreshToken(
            @RequestBody CreateAccessTokenRequest requestBody) {

        Optional<String> optionalGrantType = Optional.ofNullable(requestBody.getGrantType());
        if (optionalGrantType.isEmpty() || !requestBody.getGrantType().equals("refresh_token")) {
                throw new CustomJwtException(HttpStatus.BAD_REQUEST, CustomJwtErrorInfo.BAD_REQUEST, "Request was missing the 'grantType' parameter.");
        }

        String newAccessToken = newTokenService.createNewAccessToken(requestBody.getRefreshToken());
        String refreshToken = refreshTokenService.setRefreshTokenByEmail(tokenService.getEmail(newAccessToken));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateAccessTokenResponse(
                        newAccessToken,
                        "Bearer",
                        tokenService.getAccessTokenExpiresInToMillis(),
                        refreshToken));
    }
}
