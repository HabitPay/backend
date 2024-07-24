package com.habitpay.habitpay.domain.refreshtoken.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habitpay.habitpay.docs.springrestdocs.AbstractRestDocsTests;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.refreshtoken.application.RefreshTokenCreationService;
import com.habitpay.habitpay.domain.refreshtoken.dto.CreateAccessTokenRequest;
import com.habitpay.habitpay.domain.refreshtoken.dto.CreateAccessTokenResponse;
import com.habitpay.habitpay.global.config.jwt.TokenProvider;
import com.habitpay.habitpay.global.config.jwt.TokenService;
import com.habitpay.habitpay.global.response.SuccessResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Duration;

import static org.mockito.BDDMockito.given;

@WebMvcTest(TokenApi.class)
public class RefreshTokenApiTest extends AbstractRestDocsTests {

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    RefreshTokenCreationService refreshTokenCreationService;

    @MockBean
    TokenProvider tokenProvider;

    @MockBean
    TokenService tokenService;


    @Test
    @DisplayName("")
    void createNewAccessTokenAndNewRefreshToken() throws Exception {

        //given
        Member mockMember = Member.builder()
                .id(1L)
                .nickname("test user")
                .email("test_user@test.com")
                .build();

        CreateAccessTokenRequest tokenRequest = CreateAccessTokenRequest.builder()
                .grantType("refreshToken")
                .refreshToken(tokenProvider.generateRefreshToken(mockMember, Duration.ofHours(2)))
                .build();

        CreateAccessTokenResponse tokenResponse = CreateAccessTokenResponse.builder()
                .accessToken(tokenProvider.generateToken(mockMember, Duration.ofMinutes(30)))
                .tokenType("Bearer")
                .expiresIn(Duration.ofMinutes(30).toMillis())
                .refreshToken(tokenProvider.generateRefreshToken(mockMember, Duration.ofHours(2)))
                .build();

        given(refreshTokenCreationService.createNewAccessTokenAndNewRefreshToken(tokenRequest))
                .willReturn(SuccessResponse.of("새로운 액세스 토큰 및 리프레시 토큰이 성공적으로 발급되었습니다.", ));
    }
}
