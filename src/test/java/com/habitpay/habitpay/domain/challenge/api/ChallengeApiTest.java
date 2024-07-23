package com.habitpay.habitpay.domain.challenge.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habitpay.habitpay.docs.springrestdocs.AbstractRestDocsTests;
import com.habitpay.habitpay.domain.challenge.application.ChallengeCreationService;
import com.habitpay.habitpay.domain.challenge.application.ChallengeDetailsService;
import com.habitpay.habitpay.domain.challenge.application.ChallengePatchService;
import com.habitpay.habitpay.domain.challenge.application.ChallengeSearchService;
import com.habitpay.habitpay.domain.challenge.dto.*;
import com.habitpay.habitpay.domain.member.domain.Member;
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

import java.time.ZonedDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
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
    ChallengePatchService challengePatchService;

    @MockBean
    ChallengeSearchService challengeSearchService;

    @MockBean
    TokenService tokenService;

    @MockBean
    TokenProvider tokenProvider;

    @Test
    @WithMockOAuth2User
    @DisplayName("나의 챌린지 참여 목록 조회")
    void getEnrolledChallengeList() throws Exception {

        // given
        ChallengeEnrolledListItemResponse response = ChallengeEnrolledListItemResponse.builder()
                .title("챌린지 제목")
                .description("챌린지 설명")
                .startDate(ZonedDateTime.now())
                .endDate(ZonedDateTime.now().plusDays(5))
                .stopDate(null)
                .numberOfParticipants(1)
                .participatingDays(1 << 2)
                .totalFee(1000)
                .isPaidAll(false)
                .hostProfileImage("챌린지 주최자 프로필 이미지")
                .isMemberGivenUp(false)
                .successCount(4)
                .isTodayParticipatingDay(true)
                .isParticipatedToday(false)
                .build();

        given(challengeSearchService.getEnrolledChallengeList(any(Member.class)))
                .willReturn(SuccessResponse.of("", List.of(response)));

        // when
        ResultActions result = mockMvc.perform(get("/api/challenges/me")
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN"));

        // then
        result.andExpect(status().isOk())
                .andDo(document("challenge/get-my-challenge-list",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data[].title").description("챌린지 제목"),
                                fieldWithPath("data[].description").description("챌린지 설명"),
                                fieldWithPath("data[].startDate").description("챌린지 시작 일시"),
                                fieldWithPath("data[].endDate").description("챌린지 종료 일시"),
                                fieldWithPath("data[].stopDate").description("챌린지 중단 일시"),
                                fieldWithPath("data[].numberOfParticipants").description("챌린지 참여 인"),
                                fieldWithPath("data[].participatingDays").description("챌린지 참여 요일"),
                                fieldWithPath("data[].totalFee").description("나의 벌금 합계"),
                                fieldWithPath("data[].isPaidAll").description("최종 정산 여부"),
                                fieldWithPath("data[].hostProfileImage").description("챌린지 주최자 프로필 이미지"),
                                fieldWithPath("data[].isMemberGivenUp").description("현재 사용자의 챌린지 포기 여부"),
                                fieldWithPath("data[].successCount").description("챌린지 인증 성공 횟수"),
                                fieldWithPath("data[].isTodayParticipatingDay").description("오늘 요일 == 챌린지 참여 요일"),
                                fieldWithPath("data[].isParticipatedToday").description("오늘 챌린지 참여 여부")
                        )
                ));
    }

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
                .stopDate(null)
                .numberOfParticipants(1)
                .participatingDays(1 << 2)
                .feePerAbsence(1000)
                .isPaidAll(false)
                .hostNickname("챌린지 주최자 닉네임")
                .hostProfileImage("챌린지 주최자 프로필 이미지")
                .isHost(true)
                .isMemberEnrolledInChallenge(true)
                .build();

        given(challengeDetailsService.getChallengeDetails(anyLong(), anyLong()))
                .willReturn(SuccessResponse.of("", challengeDetailsResponse));

        // when
        ResultActions result = mockMvc.perform(get("/api/challenges/{id}", 1L)
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN"));

        // then
        result.andExpect(status().isOk())
                .andDo(document("challenge/get-challenge-details",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data.title").description("챌린지 제목"),
                                fieldWithPath("data.description").description("챌린지 설명"),
                                fieldWithPath("data.startDate").description("챌린지 시작 일시"),
                                fieldWithPath("data.endDate").description("챌린지 종료 일시"),
                                fieldWithPath("data.stopDate").description("챌린지 중단 일시"),
                                fieldWithPath("data.numberOfParticipants").description("챌린지 참여 인"),
                                fieldWithPath("data.participatingDays").description("챌린지 참여 요일"),
                                fieldWithPath("data.feePerAbsence").description("미참여 1회당 벌금"),
                                fieldWithPath("data.isPaidAll").description("최종 정산 여부"),
                                fieldWithPath("data.hostNickname").description("챌린지 주최자 닉네임"),
                                fieldWithPath("data.hostProfileImage").description("챌린지 주최자 프로필 이미지"),
                                fieldWithPath("data.isHost").description("현재 접속한 사용자 == 챌린지 주최자"),
                                fieldWithPath("data.isMemberEnrolledInChallenge").description("현재 접속한 사용자의 챌린지 참여 여부")
                        )
                ));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("챌린지 생성")
    void createChallenge() throws Exception {

        // given
        ChallengeCreationRequest challengeCreationRequest = ChallengeCreationRequest.builder()
                .title("챌린지 제목")
                .description("챌린지 설명")
                .startDate(ZonedDateTime.now().plusHours(1))
                .endDate(ZonedDateTime.now().plusDays(5))
                .participatingDays((byte) (1 << 2))
                .feePerAbsence(1000)
                .build();
        ChallengeCreationResponse challengeCreationResponse = ChallengeCreationResponse.builder()
                .hostNickname("IamHost")
                .challengeId(1L)
                .title("챌린지 제목")
                .description("챌린지 설명")
                .startDate(ZonedDateTime.now().plusHours(1))
                .endDate(ZonedDateTime.now().plusDays(5))
                .participatingDays((byte) (1 << 2))
                .feePerAbsence(1000)
                .build();

        given(challengeCreationService.createChallenge(any(ChallengeCreationRequest.class), any(Member.class)))
                .willReturn(SuccessResponse.of("챌린지가 생성되었습니다.", challengeCreationResponse));

        // when
        ResultActions result = mockMvc.perform(post("/api/challenges")
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN")
                .content(objectMapper.writeValueAsString(challengeCreationRequest))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andDo(document("challenge/create-challenge",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                        ),
                        requestFields(
                                fieldWithPath("title").description("챌린지 제목"),
                                fieldWithPath("description").description("챌린지 설명"),
                                fieldWithPath("startDate").description("챌린지 시작 일시"),
                                fieldWithPath("endDate").description("챌린지 종료 일시"),
                                fieldWithPath("participatingDays").description("챌린지 참여 요일"),
                                fieldWithPath("feePerAbsence").description("미참여 1회당 벌금")
                        ),
                        responseFields(
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data.hostNickname").description("챌린지 주최자 닉네임"),
                                fieldWithPath("data.challengeId").description("챌린지 ID"),
                                fieldWithPath("data.title").description("챌린지 제목"),
                                fieldWithPath("data.description").description("챌린지 설명"),
                                fieldWithPath("data.startDate").description("챌린지 시작 일시"),
                                fieldWithPath("data.endDate").description("챌린지 종료 일시"),
                                fieldWithPath("data.participatingDays").description("챌린지 참여 요일"),
                                fieldWithPath("data.feePerAbsence").description("1회당 미참여 벌금")
                        )
                ));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("챌린지 정보 수정")
    void patchChallenge() throws Exception {

        // given
        ChallengePatchRequest challengePatchRequest = ChallengePatchRequest.builder()
                .description("챌린지 설명")
                .build();
        ChallengePatchResponse challengePatchResponse = ChallengePatchResponse.builder()
                .title("챌린지 제목")
                .description("챌린지 설명")
                .startDate(ZonedDateTime.now().plusHours(1))
                .endDate(ZonedDateTime.now().plusDays(5))
                .participatingDays((byte) (1 << 2))
                .feePerAbsence(1000)
                .build();

        given(challengePatchService.patch(anyLong(), any(ChallengePatchRequest.class), anyLong()))
                .willReturn(SuccessResponse.of("챌린지 정보 수정이 반영되었습니다.", challengePatchResponse));

        // when
        ResultActions result = mockMvc.perform(patch("/api/challenges/{id}", 1L)
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN")
                .content(objectMapper.writeValueAsString(challengePatchRequest))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andDo(document("challenge/patch-challenge",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                        ),
                        requestFields(
                                fieldWithPath("description").description("챌린지 설명")
                        ),
                        responseFields(
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data.title").description("챌린지 제목"),
                                fieldWithPath("data.description").description("챌린지 설명"),
                                fieldWithPath("data.startDate").description("챌린지 시작 일시"),
                                fieldWithPath("data.endDate").description("챌린지 종료 일시"),
                                fieldWithPath("data.participatingDays").description("챌린지 참여 요일"),
                                fieldWithPath("data.feePerAbsence").description("1회당 미참여 벌금")
                        )
                ));
    }

}
