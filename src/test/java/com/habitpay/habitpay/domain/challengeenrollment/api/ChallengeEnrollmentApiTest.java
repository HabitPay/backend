package com.habitpay.habitpay.domain.challengeenrollment.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habitpay.habitpay.docs.springrestdocs.AbstractRestDocsTests;
import com.habitpay.habitpay.domain.challengeenrollment.application.ChallengeEnrollmentCancellationService;
import com.habitpay.habitpay.domain.challengeenrollment.application.ChallengeEnrollmentService;
import com.habitpay.habitpay.domain.challengeenrollment.dto.ChallengeEnrollmentResponse;
import com.habitpay.habitpay.domain.challengeenrollment.exception.AlreadyEnrolledChallengeException;
import com.habitpay.habitpay.domain.challengeenrollment.exception.AlreadyGivenUpChallengeException;
import com.habitpay.habitpay.domain.challengeenrollment.exception.NotEnrolledChallengeException;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.global.config.jwt.TokenProvider;
import com.habitpay.habitpay.global.config.jwt.TokenService;
import com.habitpay.habitpay.global.error.exception.BadRequestException;
import com.habitpay.habitpay.global.error.exception.ErrorCode;
import com.habitpay.habitpay.global.response.SuccessCode;
import com.habitpay.habitpay.global.response.SuccessResponse;
import com.habitpay.habitpay.global.security.WithMockOAuth2User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

import java.time.ZonedDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChallengeEnrollmentApi.class)
public class ChallengeEnrollmentApiTest extends AbstractRestDocsTests {

    static final String AUTHORIZATION_HEADER_PREFIX = "Bearer ";

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ChallengeEnrollmentService challengeEnrollmentService;

    @MockBean
    ChallengeEnrollmentCancellationService challengeEnrollmentCancellationService;

    @MockBean
    TokenService tokenService;

    @MockBean
    TokenProvider tokenProvider;

    @Test
    @WithMockOAuth2User
    @DisplayName("챌린지 등록")
    void enrollChallenge() throws Exception {

        // given
        ChallengeEnrollmentResponse challengeEnrollmentResponse = ChallengeEnrollmentResponse.builder()
                .challengeId(1L)
                .memberId(1L)
                .enrolledDate(ZonedDateTime.now())
                .build();

        given(challengeEnrollmentService.enroll(anyLong(), any(Member.class)))
                .willReturn(SuccessResponse.of(SuccessCode.ENROLL_CHALLENGE_SUCCESS, challengeEnrollmentResponse));

        // when
        ResultActions result = mockMvc.perform(post("/api/challenges/{id}/enroll", 1L)
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN"));

        // then
        result.andExpect(status().isOk())
                .andDo(document("challenge/enroll-challenge",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data.challengeId").description("챌린지 ID"),
                                fieldWithPath("data.memberId").description("사용자 ID"),
                                fieldWithPath("data.enrolledDate").description("챌린지 등록 일시")
                        )
                ));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("챌린지 등록 예외처리 - 챌린지 등록 시간 초과 (400 Bad Request)")
    void enrollChallengeInvalidRegistrationTimeException() throws Exception {

        // given
        given(challengeEnrollmentService.enroll(anyLong(), any(Member.class)))
                .willThrow(new BadRequestException(ErrorCode.INVALID_CHALLENGE_REGISTRATION_TIME));

        // when
        ResultActions result = mockMvc.perform(post("/api/challenges/{id}/enroll", 1L)
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN"));

        // then
        result.andExpect(status().isBadRequest())
                .andDo(document("challenge/enroll-challenge-invalid-registration-time-exception",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("code").description("오류 응답 코드"),
                                fieldWithPath("message").description("오류 메세지")
                        )
                ));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("챌린지 등록 예외처리 - 이미 참여한 챌린지 (409 Conflict)")
    void enrollChallengeAlreadyEnrolledException() throws Exception {

        // given
        given(challengeEnrollmentService.enroll(anyLong(), any(Member.class)))
                .willThrow(new AlreadyEnrolledChallengeException(anyLong(), anyLong()));

        // when
        ResultActions result = mockMvc.perform(post("/api/challenges/{id}/enroll", 1L)
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN"));

        // then
        result.andExpect(status().isConflict())
                .andDo(document("challenge/enroll-challenge-already-enrolled-exception",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("code").description("오류 응답 코드"),
                                fieldWithPath("message").description("오류 메세지")
                        )
                ));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("챌린지 등록 취소")
    void cancelChallenge() throws Exception {

        // given
        given(challengeEnrollmentCancellationService.cancel(anyLong(), any(Member.class)))
                .willReturn(SuccessResponse.of(SuccessCode.CANCEL_CHALLENGE_ENROLLMENT_SUCCESS));

        // when
        ResultActions result = mockMvc.perform(post("/api/challenges/{id}/cancel", 1L)
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN"));

        // then
        result.andExpect(status().isOk())
                .andDo(document("challenge/cancel-challenge-enrollment",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data").description("null")
                        )
                ));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("챌린지 등록 취소 예외처리 - 참여한 챌린지가 없는 경우 (400 Bad Request)")
    void cancelChallengeNotEnrolledException() throws Exception {

        // given
        given(challengeEnrollmentCancellationService.cancel(anyLong(), any(Member.class)))
                .willThrow(new NotEnrolledChallengeException(anyLong(), anyLong()));

        // when
        ResultActions result = mockMvc.perform(post("/api/challenges/{id}/cancel", 1L)
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN"));

        // then
        result.andExpect(status().isBadRequest())
                .andDo(document("challenge/cancel-challenge-enrollment-not-enrolled-exception",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("code").description("오류 응답 코드"),
                                fieldWithPath("message").description("오류 메세지")
                        )
                ));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("챌린지 등록 취소 예외처리 - 챌린지 주최자가 등록 취소 하는 경우 (400 Bad Request)")
    void cancelChallengeHostNotAllowedException() throws Exception {

        // given
        given(challengeEnrollmentCancellationService.cancel(anyLong(), any(Member.class)))
                .willThrow(new BadRequestException(ErrorCode.NOT_ALLOWED_TO_CANCEL_ENROLLMENT_OF_HOST));

        // when
        ResultActions result = mockMvc.perform(post("/api/challenges/{id}/cancel", 1L)
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN"));

        // then
        result.andExpect(status().isBadRequest())
                .andDo(document("challenge/cancel-challenge-enrollment-host-not-allowed-exception",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("code").description("오류 응답 코드"),
                                fieldWithPath("message").description("오류 메세지")
                        )
                ));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("챌린지 등록 취소 예외처리 - 챌린지 등록 취소 시간이 지난 경우 (400 Bad Request)")
    void cancelChallengeInvalidCancellationTimeException() throws Exception {

        // given
        given(challengeEnrollmentCancellationService.cancel(anyLong(), any(Member.class)))
                .willThrow(new BadRequestException(ErrorCode.INVALID_CHALLENGE_CANCELLATION_TIME));

        // when
        ResultActions result = mockMvc.perform(post("/api/challenges/{id}/cancel", 1L)
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN"));

        // then
        result.andExpect(status().isBadRequest())
                .andDo(document("challenge/cancel-challenge-enrollment-invalid-cancellation-time-exception",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("code").description("오류 응답 코드"),
                                fieldWithPath("message").description("오류 메세지")
                        )
                ));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("챌린지 중도 포기")
    void giveUpChallenge() throws Exception {

        // given
        given(challengeEnrollmentCancellationService.giveUp(anyLong(), any(Member.class)))
                .willReturn(SuccessResponse.of(SuccessCode.GIVING_UP_CHALLENGE));

        // when
        ResultActions result = mockMvc.perform(post("/api/challenges/{id}/give-up", 1L)
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN"));

        // then
        result.andExpect(status().isOk())
                .andDo(document("challenge/give-up-challenge",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data").description("null")
                        )
                ));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("챌린지 중도 포기 예외처리 - 챌린지 시작 시간 이전 (400 Bad Request)")
    void tooEarlyGivingUpChallengeException() throws Exception {

        // given
        given(challengeEnrollmentCancellationService.giveUp(anyLong(), any(Member.class)))
                .willThrow(new BadRequestException(ErrorCode.TOO_EARLY_GIVEN_UP_CHALLENGE));

        // when
        ResultActions result = mockMvc.perform(post("/api/challenges/{id}/give-up", 1L)
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN"));

        // then
        result.andExpect(status().isBadRequest())
                .andDo(document("challenge/giving-up-challenge-too-early-exception",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("code").description("오류 응답 코드"),
                                fieldWithPath("message").description("오류 메세지")
                        )
                ));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("챌린지 중도 포기 예외처리 - 이미 중도 포기한 경우 (400 Bad Request)")
    void alreadyGivenUpChallengeException() throws Exception {

        // given
        given(challengeEnrollmentCancellationService.giveUp(anyLong(), any(Member.class)))
                .willThrow(new AlreadyGivenUpChallengeException(anyLong(), anyLong()));

        // when
        ResultActions result = mockMvc.perform(post("/api/challenges/{id}/give-up", 1L)
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN"));

        // then
        result.andExpect(status().isBadRequest())
                .andDo(document("challenge/already-given-up-challenge-exception",
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
