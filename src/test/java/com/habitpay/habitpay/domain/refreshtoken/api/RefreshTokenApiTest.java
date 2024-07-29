package com.habitpay.habitpay.domain.refreshtoken.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habitpay.habitpay.docs.springrestdocs.AbstractRestDocsTests;
import com.habitpay.habitpay.domain.refreshtoken.application.RefreshTokenCreationService;
import com.habitpay.habitpay.domain.refreshtoken.dto.CreateAccessTokenRequest;
import com.habitpay.habitpay.domain.refreshtoken.dto.CreateAccessTokenResponse;
import com.habitpay.habitpay.global.config.jwt.TokenProvider;
import com.habitpay.habitpay.global.config.jwt.TokenService;
import com.habitpay.habitpay.global.error.CustomJwtErrorInfo;
import com.habitpay.habitpay.global.response.SuccessResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


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
    @DisplayName("토큰 리프레시 요청")
    void createNewAccessTokenAndNewRefreshToken() throws Exception {

        //given
        CreateAccessTokenRequest tokenRequest = CreateAccessTokenRequest.builder()
                .grantType("refreshToken")
                .refreshToken("eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyIjoiYWxpY2UifQ.DUMMY_SIGNATURE2")
                .build();

        CreateAccessTokenResponse tokenResponse = CreateAccessTokenResponse.builder()
                .accessToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0In0.DUMMY_SIGNATURE1")
                .tokenType("Bearer")
                .expiresIn(Duration.ofMinutes(30).toMillis())
                .refreshToken("eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyIjoiYWxpY2UifQ.DUMMY_SIGNATURE3")
                .build();

        given(refreshTokenCreationService.createNewAccessTokenAndNewRefreshToken(any(CreateAccessTokenRequest.class)))
                .willReturn(SuccessResponse.of("새로운 액세스 토큰 및 리프레시 토큰이 성공적으로 발급되었습니다.", tokenResponse));

        //when
        ResultActions result = mockMvc.perform(post("/token")
                .content(objectMapper.writeValueAsString(tokenRequest))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
                .andDo(document("refreshToken/create-new-access-token-and-new-refresh-token",
                        requestFields(
                                fieldWithPath("grantType").description("클라이언트가 액세스 토큰을 요청할 때 사용하는 인증 방법. \"refreshToken\""),
                                fieldWithPath("refreshToken").description("클라이언트가 보관하던 리프레시 토큰")
                        ),
                        responseFields(
                                fieldWithPath("message").description("메시지"),
                                fieldWithPath("data.accessToken").description("새로 발급한 액세스 토큰"),
                                fieldWithPath("data.tokenType").description("발급한 토큰의 유형. \"Bearer\""),
                                fieldWithPath("data.expiresIn").description("액세스 토큰의 유효 기간"),
                                fieldWithPath("data.refreshToken").description("새로 발급한 리프레시 토큰. 추후 새 액세스 토큰 발급 시 이용된다.")
                        )
                ));

    }

    @Test
    @DisplayName("토큰 리프레시 실패 : 400 Bad Request")
    void return400WhenInvalidRequest() throws Exception {

        //given
        CreateAccessTokenRequest tokenRequest = CreateAccessTokenRequest.builder()
                .refreshToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0In0.DUMMY_SIGNATURE1")
                .build();

        given(refreshTokenCreationService.createNewAccessTokenAndNewRefreshToken(any(CreateAccessTokenRequest.class)))
                .willThrow(new CustomJwtException(HttpStatus.BAD_REQUEST, CustomJwtErrorInfo.BAD_REQUEST, "인증 방법을 알 수 없습니다."));

        //when
        ResultActions result = mockMvc.perform(post("/token")
                .content(objectMapper.writeValueAsString(tokenRequest))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isBadRequest())
                .andDo(document("refreshToken/return-400-when-invalid-request",
                        responseFields(
                                fieldWithPath("error").description("에러 메시지"),
                                fieldWithPath("errorDescription").description("에러 발생 이유")
                        )
                ));

    }

    @Test
    @DisplayName("토큰 리프레시 실패 : 401 Unauthorized")
    void return401WhenInvalidToken() throws Exception {

        //given
        CreateAccessTokenRequest tokenRequest = CreateAccessTokenRequest.builder()
                .grantType("refreshToken")
                .refreshToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0In0.EXPIRED_DUMMY_SIGNATURE1")
                .build();

        given(refreshTokenCreationService.createNewAccessTokenAndNewRefreshToken(any(CreateAccessTokenRequest.class)))
                .willThrow(new CustomJwtException(HttpStatus.UNAUTHORIZED, CustomJwtErrorInfo.UNAUTHORIZED, "JWT expired at 2024-07-15T11:29:31Z."));

        //when
        ResultActions result = mockMvc.perform(post("/token")
                .content(objectMapper.writeValueAsString(tokenRequest))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isUnauthorized())
                .andDo(document("refreshToken/return-401-when-invalid-token",
                        responseFields(
                                fieldWithPath("error").description("에러 메시지"),
                                fieldWithPath("errorDescription").description("에러 발생 이유")
                        )
                ));

    }

    @Test
    @DisplayName("토큰 리프레시 실패 : 403 Forbidden")
    void return404WhenInsufficientScope() throws Exception {

        //given
        CreateAccessTokenRequest tokenRequest = CreateAccessTokenRequest.builder()
                .grantType("refreshToken")
                .refreshToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0In0.GUEST_DUMMY_SIGNATURE1")
                .build();

        given(refreshTokenCreationService.createNewAccessTokenAndNewRefreshToken(any(CreateAccessTokenRequest.class)))
                .willThrow(new CustomJwtException(HttpStatus.FORBIDDEN, CustomJwtErrorInfo.FORBIDDEN, "공지 포스트는 챌린지 호스트만 작성할 수 있습니다."));

        //when
        ResultActions result = mockMvc.perform(post("/token")
                .content(objectMapper.writeValueAsString(tokenRequest))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isForbidden())
                .andDo(document("refreshToken/return-403-when-insufficient-scope",
                        responseFields(
                                fieldWithPath("error").description("에러 메시지"),
                                fieldWithPath("errorDescription").description("에러 발생 이유")
                        )
                ));

    }
}
