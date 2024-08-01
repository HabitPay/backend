package com.habitpay.habitpay.domain.refreshtoken.api;

import com.habitpay.habitpay.domain.refreshtoken.application.RefreshTokenCreationService;
import com.habitpay.habitpay.domain.refreshtoken.dto.CreateAccessTokenRequest;
import com.habitpay.habitpay.domain.refreshtoken.dto.CreateAccessTokenResponse;
import com.habitpay.habitpay.global.response.SuccessResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@Slf4j
public class TokenApi {

    private final RefreshTokenCreationService refreshTokenCreationService;

    @PostMapping("/token")
    public SuccessResponse<CreateAccessTokenResponse> createNewAccessTokenAndNewRefreshToken(
            HttpServletRequest request,
            HttpServletResponse response) {

        return refreshTokenCreationService.createNewAccessTokenAndNewRefreshToken(request, response);
    }
}
