package com.habitpay.habitpay.domain.refreshToken.api;

import com.habitpay.habitpay.domain.refreshToken.application.NewTokenService;
import com.habitpay.habitpay.domain.refreshToken.dto.CreateAccessTokenRequest;
import com.habitpay.habitpay.domain.refreshToken.dto.CreateAccessTokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TokenApiController {

    private final NewTokenService newTokenService;

    @PostMapping("/api/token")
    public ResponseEntity<CreateAccessTokenResponse> createNewAccessToken(@RequestBody CreateAccessTokenRequest requestBody) {

        String requestIp = newTokenService.getClientIpAddress();
        String newAccessToken = newTokenService.createNewAccessToken(requestBody.getRefreshToken(), requestIp);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateAccessTokenResponse(newAccessToken));
    }
}
