package com.habitpay.habitpay.domain.challengeenrollment.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habitpay.habitpay.docs.springrestdocs.AbstractRestDocsTests;
import com.habitpay.habitpay.domain.challengeenrollment.application.ChallengeEnrollmentCancellationService;
import com.habitpay.habitpay.domain.challengeenrollment.application.ChallengeEnrollmentService;
import com.habitpay.habitpay.domain.challengeenrollment.dto.ChallengeEnrollmentResponse;
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
import org.springframework.test.web.servlet.ResultActions;

import java.time.ZonedDateTime;

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

        given(challengeEnrollmentService.enroll(anyLong(), anyLong()))
                .willReturn(SuccessResponse.of("챌린지에 정상적으로 등록했습니다.", challengeEnrollmentResponse));

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


}
