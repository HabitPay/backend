package com.habitpay.habitpay.domain.refreshtoken.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habitpay.habitpay.docs.springrestdocs.AbstractRestDocsTests;
import com.habitpay.habitpay.domain.refreshtoken.application.RefreshTokenCreationService;
import com.habitpay.habitpay.domain.refreshtoken.dto.CreateAccessTokenRequest;
import com.habitpay.habitpay.domain.refreshtoken.dto.CreateAccessTokenResponse;
import com.habitpay.habitpay.global.config.jwt.TokenProvider;
import com.habitpay.habitpay.global.config.jwt.TokenService;
import com.habitpay.habitpay.global.error.exception.BadRequestException;
import com.habitpay.habitpay.global.error.exception.ErrorCode;
import com.habitpay.habitpay.global.error.exception.ForbiddenException;
import com.habitpay.habitpay.global.error.exception.UnauthorizedException;
import com.habitpay.habitpay.global.response.SuccessCode;
import com.habitpay.habitpay.global.response.SuccessResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
        CreateAccessTokenResponse tokenResponse = CreateAccessTokenResponse.builder()
                .accessToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0In0.DUMMY_SIGNATURE1")
                .tokenType("Bearer")
                .expiresIn(Duration.ofMinutes(30).toMillis())
                .build();

        given(refreshTokenCreationService.createNewAccessTokenAndNewRefreshToken(any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .willReturn(SuccessResponse.of(SuccessCode.REFRESH_TOKEN_SUCCESS, tokenResponse));

        //when
        ResultActions result = mockMvc.perform(post("/api/token")
                .cookie(new Cookie("refresh", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0In0.DUMMY_SIGNATURE2"))
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("refreshToken/create-new-access-token-and-new-refresh-token",
                        responseFields(
                                fieldWithPath("message").description("메시지"),
                                fieldWithPath("data.accessToken").description("새로 발급한 액세스 토큰"),
                                fieldWithPath("data.tokenType").description("발급한 토큰의 유형. \"Bearer\""),
                                fieldWithPath("data.expiresIn").description("액세스 토큰의 유효 기간")
                        )
                ));

    }

    @Test
    @DisplayName("토큰 리프레시 실패 : 400 Bad Request")
    void return400WhenInvalidRequest() throws Exception {

        //given
       given(refreshTokenCreationService.createNewAccessTokenAndNewRefreshToken(any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .willThrow(new BadRequestException(ErrorCode.BAD_REQUEST));

        //when
        ResultActions result = mockMvc.perform(post("/api/token"));

        //then
        result.andExpect(status().isBadRequest())
                .andDo(document("refreshToken/return-400-when-invalid-request",
                        responseFields(
                                fieldWithPath("code").description("에러 코드"),
                                fieldWithPath("message").description("에러 메시지")
                        )
                ));

    }

    @Test
    @DisplayName("토큰 리프레시 실패 : 401 Unauthorized")
    void return401WhenInvalidToken() throws Exception {

        //given
        given(refreshTokenCreationService.createNewAccessTokenAndNewRefreshToken(any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .willThrow(new UnauthorizedException(ErrorCode.UNAUTHORIZED));

        //when
        ResultActions result = mockMvc.perform(post("/api/token")
                .cookie(new Cookie("refresh", "expiredGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0In0.DUMMY_SIGNATURE2"))
        );

        //then
        result.andExpect(status().isUnauthorized())
                .andDo(document("refreshToken/return-401-when-invalid-token",
                        responseFields(
                                fieldWithPath("code").description("에러 코드"),
                                fieldWithPath("message").description("에러 메시지")
                        )
                ));

    }

    @Test
    @DisplayName("토큰 리프레시 실패 : 403 Forbidden")
    void return404WhenInsufficientScope() throws Exception {

        //given
        given(refreshTokenCreationService.createNewAccessTokenAndNewRefreshToken(any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .willThrow(new ForbiddenException(ErrorCode.FORBIDDEN));

        //when
        ResultActions result = mockMvc.perform(post("/api/token")
                .cookie(new Cookie("refresh", "justGuestGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0In0.DUMMY_SIGNATURE2"))
        );

        //then
        result.andExpect(status().isForbidden())
                .andDo(document("refreshToken/return-403-when-insufficient-scope",
                        responseFields(
                                fieldWithPath("code").description("에러 코드"),
                                fieldWithPath("message").description("에러 메시지")
                        )
                ));

    }
}
