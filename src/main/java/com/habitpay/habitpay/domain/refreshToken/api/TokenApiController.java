package com.habitpay.habitpay.domain.refreshToken.api;

import com.habitpay.habitpay.domain.refreshToken.application.NewTokenService;
import com.habitpay.habitpay.domain.refreshToken.dto.CreateAccessTokenRequest;
import com.habitpay.habitpay.domain.refreshToken.dto.CreateAccessTokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    private final NewTokenService newTokenService;

    @PostMapping("/token")
    public ResponseEntity<CreateAccessTokenResponse> createNewAccessToken(@RequestBody CreateAccessTokenRequest requestBody) {

        String requestIp = newTokenService.getClientIpAddress();
        String newAccessToken = newTokenService.createNewAccessToken(requestBody.getRefreshToken(), requestIp);

        log.info("Client IP Address : {}", requestIp);

        // todo : 액세스 토큰 재발급하면서, 새 리프레시 토큰은 쿠키에 넣어줘야 함 : refresh token rotation

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateAccessTokenResponse(newAccessToken));
    }

    // todo : 테스트 후 삭제
    @PostMapping("/api/test")
    public ResponseEntity<String> apiTest(@RequestBody String request, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("api test ok : " + principal.getName());
    }

    @GetMapping("/api/get_test")
    public String getTest() {
        return "test ok!";
    }
}
