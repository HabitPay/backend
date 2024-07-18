package com.habitpay.habitpay.domain.challenge.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habitpay.habitpay.docs.springrestdocs.AbstractRestDocsTests;
import com.habitpay.habitpay.domain.challenge.application.ChallengeCreationService;
import com.habitpay.habitpay.domain.challenge.application.ChallengeDetailsService;
import com.habitpay.habitpay.domain.challenge.application.ChallengeUpdateService;
import com.habitpay.habitpay.domain.challenge.dto.ChallengeDetailsResponse;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChallengeApi.class)
public class ChallengeApiTest extends AbstractRestDocsTests {

    static final String AUTHORIZATION_HEADER_PREFIX = "Bearer ";

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ChallengeDetailsService challengeDetailsService;

    @MockBean
    ChallengeCreationService challengeCreationService;

    @MockBean
    ChallengeUpdateService challengeUpdateService;

    @MockBean
    TokenService tokenService;

    @MockBean
    TokenProvider tokenProvider;

    @Test
    @WithMockOAuth2User
    @DisplayName("챌린지 상세 정보 조회")
    void getChallengeDetails() throws Exception {

        // given
        ChallengeDetailsResponse challengeDetailsResponse = ChallengeDetailsResponse.builder()
                .title("챌린지 제목")
                .description("챌린지 설명")
                .startDate(ZonedDateTime.now())
                .endDate(ZonedDateTime.now().plusDays(5))
                .hostNickname("챌린지 주최자 닉네임")
                .hostProfileImage("챌린지 주최자 프로필 이미지")
                .isHost(true)
                .participatingDays(1 << 2)
                .feePerAbsence(1000)
                .isMemberEnrolledInChallenge(true)
                .build();

        given(challengeDetailsService.getChallengeDetails(anyLong(), anyLong()))
                .willReturn(SuccessResponse.of("", challengeDetailsResponse));

        // when
        ResultActions result = mockMvc.perform(get("/api/challenges/{id}", 1L)
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN"));

        // then
        result.andExpect(status().isOk())
                .andDo(document("member/get-challenge-details",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data.title").description("챌린지 제목"),
                                fieldWithPath("data.description").description("챌린지 설명"),
                                fieldWithPath("data.startDate").description("챌린지 시작 일시"),
                                fieldWithPath("data.endDate").description("챌린지 종료 일시"),
                                fieldWithPath("data.participatingDays").description("챌린지 참여 요일"),
                                fieldWithPath("data.feePerAbsence").description("1회당 미참여 벌금"),
                                fieldWithPath("data.hostNickname").description("챌린지 주최자 닉네임"),
                                fieldWithPath("data.hostProfileImage").description("챌린지 주최자 프로필 이미지"),
                                fieldWithPath("data.isHost").description("현재 접속한 사용자 == 챌린지 주최자"),
                                fieldWithPath("data.isMemberEnrolledInChallenge").description("현재 접속한 사용자의 챌린지 참여 여부")
                        )
                ));
    }

}