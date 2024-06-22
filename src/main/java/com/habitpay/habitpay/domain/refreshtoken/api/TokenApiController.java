package com.habitpay.habitpay.domain.refreshtoken.api;

import com.habitpay.habitpay.domain.refreshtoken.application.RefreshTokenCreationService;
import com.habitpay.habitpay.domain.refreshtoken.dto.CreateAccessTokenRequest;
import com.habitpay.habitpay.domain.refreshtoken.dto.CreateAccessTokenResponse;
import com.habitpay.habitpay.global.config.jwt.TokenService;
import com.habitpay.habitpay.global.error.CustomJwtErrorInfo;
import com.habitpay.habitpay.domain.refreshtoken.exception.CustomJwtException;
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
    private final RefreshTokenCreationService refreshTokenCreationService;

    @PostMapping("/token")
    public ResponseEntity<CreateAccessTokenResponse> createNewAccessTokenAndNewRefreshToken(
            @RequestBody CreateAccessTokenRequest requestBody) {

        Optional<String> optionalGrantType = Optional.ofNullable(requestBody.getGrantType());
        if (optionalGrantType.isEmpty() || !requestBody.getGrantType().equals("refresh_token")) {
                throw new CustomJwtException(HttpStatus.BAD_REQUEST, CustomJwtErrorInfo.BAD_REQUEST, "Request was missing the 'grantType' parameter.");
        }

        String newAccessToken = refreshTokenCreationService.createNewAccessToken(requestBody.getRefreshToken());
        String refreshToken = refreshTokenCreationService.setRefreshTokenByEmail(tokenService.getEmail(newAccessToken));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateAccessTokenResponse(
                        newAccessToken,
                        "Bearer",
                        tokenService.getAccessTokenExpiresInToMillis(),
                        refreshToken));
    }
}
