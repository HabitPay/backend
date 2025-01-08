package com.habitpay.habitpay.domain.member.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habitpay.habitpay.docs.springrestdocs.AbstractRestDocsTests;
import com.habitpay.habitpay.domain.member.application.MemberDeleteService;
import com.habitpay.habitpay.domain.member.application.MemberDetailsService;
import com.habitpay.habitpay.domain.member.application.MemberSearchService;
import com.habitpay.habitpay.domain.member.application.MemberUpdateService;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.member.dto.*;
import com.habitpay.habitpay.domain.member.exception.InvalidNicknameException;
import com.habitpay.habitpay.domain.refreshtoken.application.RefreshTokenCreationService;
import com.habitpay.habitpay.global.config.aws.S3FileService;
import com.habitpay.habitpay.global.config.jwt.TokenProvider;
import com.habitpay.habitpay.global.config.jwt.TokenService;
import com.habitpay.habitpay.global.error.exception.ErrorCode;
import com.habitpay.habitpay.global.error.exception.InvalidValueException;
import com.habitpay.habitpay.global.response.SuccessCode;
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
    MemberDetailsService memberDetailsService;

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
                .willReturn(SuccessResponse.of(SuccessCode.NO_MESSAGE, memberProfileResponse));

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
    @DisplayName("타 사용자 포함 상세 조회")
    void getMemberDetails() throws Exception {

        // given
        MemberDetailsResponse memberDetailsResponse = MemberDetailsResponse.builder()
                .memberId(1L)
                .nickname("HabitPay")
                .imageUrl("https://picsum.photos/id/40/200/300")
                .isCurrentUser(false)
                .build();

        given(memberDetailsService.getMemberDetails(anyLong(), any(Member.class)))
                .willReturn(SuccessResponse.of(SuccessCode.NO_MESSAGE, memberDetailsResponse));

        // when
        ResultActions result = mockMvc.perform(get("/api/members/{id}", 1L)
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN"));

        // then
        result.andExpect(status().isOk())
                .andDo(document("member/get-member-details",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data.memberId").description("사용자 멤버 아이디"),
                                fieldWithPath("data.nickname").description("사용자 닉네임"),
                                fieldWithPath("data.imageUrl").description("사용자 이미지 URL"),
                                fieldWithPath("data.isCurrentUser").description("요청 받은 데이터의 주인이 요청한 사용자인지 여부")
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
        SuccessResponse<NicknameDto> successResponse = SuccessResponse.of(SuccessCode.NICKNAME_UPDATE_SUCCESS.getMessage(), nicknameDto);
        given(memberUpdateService.updateNickname(any(NicknameDto.class), any(Member.class)))
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
    @DisplayName("사용자 닉네임 변경 예외처리 - 닉네임 규칙 불일치 (400 Bad Request)")
    void patchNicknameInvalidRuleException() throws Exception {

        // given
        String invalidNickname = "invalid.#_!nickname";
        NicknameDto nicknameDto = NicknameDto.builder()
                .nickname(invalidNickname)
                .build();
        given(memberUpdateService.updateNickname(any(NicknameDto.class), any(Member.class)))
                .willThrow(new InvalidNicknameException(invalidNickname, ErrorCode.INVALID_NICKNAME_RULE));

        // when
        ResultActions result = mockMvc.perform(patch("/api/member/nickname")
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN")
                .content(objectMapper.writeValueAsString(nicknameDto))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isBadRequest())
                .andDo(document("member/patch-member-nickname-invalid-rule-exception",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                        ),
                        requestFields(
                                fieldWithPath("nickname").description("닉네임")
                        ),
                        responseFields(
                                fieldWithPath("code").description("오류 응답 코드"),
                                fieldWithPath("message").description("오류 메세지")
                        )
                ));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("사용자 닉네임 변경 예외처리 - 이전 닉네임과 동일 (400 Bad Request)")
    void patchNicknameDuplicatedNicknameException() throws Exception {

        // given
        String duplicatedNickname = "duplicatedNickname";
        NicknameDto nicknameDto = NicknameDto.builder()
                .nickname(duplicatedNickname)
                .build();
        given(memberUpdateService.updateNickname(any(NicknameDto.class), any(Member.class)))
                .willThrow(new InvalidNicknameException(duplicatedNickname, ErrorCode.DUPLICATED_NICKNAME));

        // when
        ResultActions result = mockMvc.perform(patch("/api/member/nickname")
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN")
                .content(objectMapper.writeValueAsString(nicknameDto))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isBadRequest())
                .andDo(document("member/patch-member-nickname-duplicated-nickname-exception",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                        ),
                        requestFields(
                                fieldWithPath("nickname").description("닉네임")
                        ),
                        responseFields(
                                fieldWithPath("code").description("오류 응답 코드"),
                                fieldWithPath("message").description("오류 메세지")
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
        SuccessResponse<ImageUpdateResponse> successResponse = SuccessResponse.of(SuccessCode.PROFILE_IMAGE_UPDATE_SUCCESS, imageUpdateResponse);
        given(memberUpdateService.updateImage(any(ImageUpdateRequest.class), any(Member.class)))
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
    @DisplayName("사용자 프로필 이미지 변경 예외처리 - 파일 크기 초과 (400 Bad Request)")
    void patchImageSizeExceededException() throws Exception {

        // given
        Long exceededFileSize = 1024L * 1024L * 1024L;
        ImageUpdateRequest imageUpdateRequest = ImageUpdateRequest.builder()
                .extension("jpg")
                .contentLength(exceededFileSize)
                .build();

        given(memberUpdateService.updateImage(any(ImageUpdateRequest.class), any(Member.class)))
                .willThrow(new InvalidValueException(String.format("size: %dMB", exceededFileSize / 1024 / 1024), ErrorCode.PROFILE_IMAGE_SIZE_TOO_LARGE));

        // when
        ResultActions result = mockMvc.perform(patch("/api/member/image")
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN")
                .content(objectMapper.writeValueAsString(imageUpdateRequest))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isBadRequest())
                .andDo(document("member/patch-member-image-size-exceeded-exception",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                        ),
                        requestFields(
                                fieldWithPath("extension").description("이미지 확장자"),
                                fieldWithPath("contentLength").description("이미지 크기")
                        ),
                        responseFields(
                                fieldWithPath("code").description("오류 응답 코드"),
                                fieldWithPath("message").description("오류 메세지")
                        )
                ));
    }


    @Test
    @WithMockOAuth2User
    @DisplayName("사용자 프로필 이미지 변경 예외처리 - 허용하지 않는 이미지 확장자 (400 Bad Request)")
    void patchImageUnsupportedExtensionException() throws Exception {

        // given
        String invalidExtension = "invalidExtension";
        ImageUpdateRequest imageUpdateRequest = ImageUpdateRequest.builder()
                .extension(invalidExtension)
                .contentLength(1024L * 1024L)
                .build();

        given(memberUpdateService.updateImage(any(ImageUpdateRequest.class), any(Member.class)))
                .willThrow(new InvalidValueException(String.format("extension: %s", invalidExtension), ErrorCode.UNSUPPORTED_IMAGE_EXTENSION));

        // when
        ResultActions result = mockMvc.perform(patch("/api/member/image")
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN")
                .content(objectMapper.writeValueAsString(imageUpdateRequest))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isBadRequest())
                .andDo(document("member/patch-member-image-unsupported-extension-exception",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                        ),
                        requestFields(
                                fieldWithPath("extension").description("이미지 확장자"),
                                fieldWithPath("contentLength").description("이미지 크기")
                        ),
                        responseFields(
                                fieldWithPath("code").description("오류 응답 코드"),
                                fieldWithPath("message").description("오류 메세지")
                        )
                ));
    }


    @Test
    @WithMockOAuth2User
    @DisplayName("회원 탈퇴")
    void deleteMember() throws Exception {

        // given
        SuccessResponse<Long> successResponse = SuccessResponse.of(SuccessCode.DELETE_MEMBER_ACCOUNT_SUCCESS, 1L);
        given(memberDeleteService.delete(any(Member.class)))
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
}
