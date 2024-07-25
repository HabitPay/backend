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
    @DisplayName("")
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
}
