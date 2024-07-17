package com.habitpay.habitpay.domain.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habitpay.habitpay.docs.springrestdocs.AbstractRestDocsTests;
import com.habitpay.habitpay.domain.member.api.MemberApi;
import com.habitpay.habitpay.domain.member.application.MemberActivationService;
import com.habitpay.habitpay.domain.member.application.MemberUpdateService;
import com.habitpay.habitpay.domain.member.application.MemberSearchService;
import com.habitpay.habitpay.domain.member.application.MemberService;
import com.habitpay.habitpay.domain.member.dto.MemberActivationRequest;
import com.habitpay.habitpay.domain.member.dto.MemberActivationResponse;
import com.habitpay.habitpay.domain.member.dto.MemberProfileResponse;
import com.habitpay.habitpay.domain.member.dto.NicknameDto;
import com.habitpay.habitpay.domain.refreshtoken.application.RefreshTokenCreationService;
import com.habitpay.habitpay.global.config.aws.S3FileService;
import com.habitpay.habitpay.global.config.jwt.TokenProvider;
import com.habitpay.habitpay.global.config.jwt.TokenService;
import com.habitpay.habitpay.global.response.SuccessResponse;
import com.habitpay.habitpay.global.security.WithMockOAuth2User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberApi.class)
public class MemberApiTest extends AbstractRestDocsTests {

    static final String AUTHORIZATION_HEADER_PREFIX = "Bearer ";

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    MemberService memberService;

    @MockBean
    MemberSearchService memberSearchService;

    @MockBean
    MemberActivationService memberActivationService;

    @MockBean
    MemberUpdateService memberUpdateService;

    @MockBean
    TokenService tokenService;

    @MockBean
    TokenProvider tokenProvider;

    @MockBean
    RefreshTokenCreationService refreshTokenCreationService;

    @MockBean
    S3FileService s3FileService;

    @Test
    @WithMockOAuth2User
    @DisplayName("사용자 조회")
    void getMember() throws Exception {

        // given
        MemberProfileResponse memberProfileResponse = MemberProfileResponse.builder()
                .nickname("HabitPay")
                .imageUrl("https://picsum.photos/id/40/200/300")
                .build();

        given(memberSearchService.getMemberProfile(anyLong()))
                .willReturn(SuccessResponse.of("", memberProfileResponse));

        // when
        ResultActions result = mockMvc.perform(get("/api/member")
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN"));

        // then
        result.andExpect(status().isOk())
                .andDo(document("member/get-member",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data.nickname").description("사용자 닉네임"),
                                fieldWithPath("data.imageUrl").description("사용자 이미지 URL")
                        )
                ));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("사용자 활성화")
    void activateMember() throws Exception {

        // given
        MemberActivationRequest memberActivationRequest = MemberActivationRequest.builder()
                .nickname("testNickname")
                .build();
        MemberActivationResponse memberActivationResponse = MemberActivationResponse.builder()
                .nickname("testNickname")
                .accessToken("ACCESS_TOKEN")
                .expiresIn(3600L)
                .tokenType(AUTHORIZATION_HEADER_PREFIX)
                .build();
        SuccessResponse<MemberActivationResponse> successResponse = SuccessResponse.of("회원가입이 완료되었습니다.", memberActivationResponse);
        given(memberActivationService.activate(any(MemberActivationRequest.class), anyLong()))
                .willReturn(successResponse);

        // when
        ResultActions result = mockMvc.perform(post("/api/member")
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN")
                .content(objectMapper.writeValueAsString(memberActivationRequest))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andDo(document("member/post-member",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                        ),
                        requestFields(
                                fieldWithPath("nickname").description("닉네임")
                        ),
                        responseFields(
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data.nickname").description("닉네임"),
                                fieldWithPath("data.accessToken").description("액세스 토큰"),
                                fieldWithPath("data.expiresIn").description("토큰 유효 시간"),
                                fieldWithPath("data.tokenType").description("토큰 타입")
                        )
                ));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("사용자 닉네임 변경")
    void patchNickname() throws Exception {

        // given
        NicknameDto nicknameDto = NicknameDto.builder()
                .nickname("testNickname")
                .build();
        // TODO: 응답 메세지 enum 으로 관리하기
        SuccessResponse<NicknameDto> successResponse = SuccessResponse.of("프로필 업데이트에 성공했습니다.", nicknameDto);
        given(memberUpdateService.updateNickname(any(NicknameDto.class), anyLong()))
                .willReturn(successResponse);

        // when
        ResultActions result = mockMvc.perform(patch("/api/member/nickname")
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN")
                .content(objectMapper.writeValueAsString(nicknameDto))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andDo(document("member/patch-member-nickname",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                        ),
                        requestFields(
                                fieldWithPath("nickname").description("닉네임")
                        ),
                        responseFields(
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data.nickname").description("닉네임")
                        )
                ));
    }
}
