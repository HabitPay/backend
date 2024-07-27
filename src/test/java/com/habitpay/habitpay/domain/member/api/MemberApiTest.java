package com.habitpay.habitpay.domain.member.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habitpay.habitpay.docs.springrestdocs.AbstractRestDocsTests;
import com.habitpay.habitpay.domain.member.application.MemberDeleteService;
import com.habitpay.habitpay.domain.member.application.MemberSearchService;
import com.habitpay.habitpay.domain.member.application.MemberUpdateService;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.member.dto.ImageUpdateRequest;
import com.habitpay.habitpay.domain.member.dto.ImageUpdateResponse;
import com.habitpay.habitpay.domain.member.dto.MemberProfileResponse;
import com.habitpay.habitpay.domain.member.dto.NicknameDto;
import com.habitpay.habitpay.domain.member.exception.MemberNotFoundException;
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
    MemberSearchService memberSearchService;

    @MockBean
    MemberUpdateService memberUpdateService;

    @MockBean
    MemberDeleteService memberDeleteService;

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

        given(memberSearchService.getMemberProfile(any(Member.class)))
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

    @Test
    @WithMockOAuth2User
    @DisplayName("사용자 이미지 변경")
    void patchImage() throws Exception {

        // given
        ImageUpdateRequest imageUpdateRequest = ImageUpdateRequest.builder()
                .extension("jpg")
                .contentLength(1024L * 1024L)
                .build();
        ImageUpdateResponse imageUpdateResponse = ImageUpdateResponse.builder()
                .preSignedUrl("https://{AWS S3 preSignedUrl to upload image file}")
                .build();
        // TODO: 응답 메세지 enum 으로 관리하기
        SuccessResponse<ImageUpdateResponse> successResponse = SuccessResponse.of("프로필 업데이트에 성공했습니다.", imageUpdateResponse);
        given(memberUpdateService.updateImage(any(ImageUpdateRequest.class), anyLong()))
                .willReturn(successResponse);

        // when
        ResultActions result = mockMvc.perform(patch("/api/member/image")
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN")
                .content(objectMapper.writeValueAsString(imageUpdateRequest))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andDo(document("member/patch-member-image",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                        ),
                        requestFields(
                                fieldWithPath("extension").description("이미지 확장자"),
                                fieldWithPath("contentLength").description("이미지 크기")
                        ),
                        responseFields(
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data.preSignedUrl").description("AWS S3 업로드를 위한 preSignedUrl")
                        )
                ));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("회원 탈퇴")
    void deleteMember() throws Exception {

        // given
        // TODO: 응답 메세지 enum 으로 관리하기
        SuccessResponse<Long> successResponse = SuccessResponse.of("정상적으로 탈퇴되었습니다.", 1L);
        given(memberDeleteService.delete(anyLong()))
                .willReturn(successResponse);

        // when
        ResultActions result = mockMvc.perform(delete("/api/member")
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andDo(document("member/delete-member",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data").description("Member ID")
                        )
                ));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("회원 탈퇴 예외처리 - 404 Not Found")
    void deleteMemberException() throws Exception {

        // given
        given(memberDeleteService.delete(anyLong()))
                .willThrow(new MemberNotFoundException(anyLong()));

        // when
        ResultActions result = mockMvc.perform(delete("/api/member")
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isNotFound())
                .andDo(document("member/delete-member-exception",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("code").description("오류 응답 코드"),
                                fieldWithPath("message").description("오류 메세지")
                        )
                ));
    }
}
