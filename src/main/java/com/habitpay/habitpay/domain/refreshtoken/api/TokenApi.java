package com.habitpay.habitpay.domain.refreshtoken.api;

import com.habitpay.habitpay.domain.refreshtoken.application.RefreshTokenCreationService;
import com.habitpay.habitpay.domain.refreshtoken.dto.CreateAccessTokenRequest;
import com.habitpay.habitpay.domain.refreshtoken.dto.CreateAccessTokenResponse;
import com.habitpay.habitpay.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
public class TokenApi {

    private final RefreshTokenCreationService refreshTokenCreationService;

    @PostMapping("/token")
    public SuccessResponse<CreateAccessTokenResponse> createNewAccessTokenAndNewRefreshToken(
            @RequestBody CreateAccessTokenRequest requestBody) {

        return SuccessResponse.of(
                "새로운 액세스 토큰 및 리프레시 토큰이 성공적으로 발급되었습니다.",
                refreshTokenCreationService.createNewAccessTokenAndNewRefreshToken(requestBody)
        );
    }
}
