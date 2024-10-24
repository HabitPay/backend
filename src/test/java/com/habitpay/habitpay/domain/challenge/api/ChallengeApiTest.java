package com.habitpay.habitpay.domain.challenge.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habitpay.habitpay.docs.springrestdocs.AbstractRestDocsTests;
import com.habitpay.habitpay.domain.challenge.application.*;
import com.habitpay.habitpay.domain.challenge.dto.*;
import com.habitpay.habitpay.domain.challenge.exception.ChallengeNotFoundException;
import com.habitpay.habitpay.domain.challenge.exception.ChallengeStartTimeInvalidException;
import com.habitpay.habitpay.domain.challenge.exception.InvalidChallengeParticipatingDaysException;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.global.config.jwt.TokenProvider;
import com.habitpay.habitpay.global.config.jwt.TokenService;
import com.habitpay.habitpay.global.config.timezone.TimeZoneProperties;
import com.habitpay.habitpay.global.error.exception.ErrorCode;
import com.habitpay.habitpay.global.error.exception.ForbiddenException;
import com.habitpay.habitpay.global.error.exception.InvalidValueException;
import com.habitpay.habitpay.global.response.PageResponse;
import com.habitpay.habitpay.global.response.SuccessCode;
import com.habitpay.habitpay.global.response.SuccessResponse;
import com.habitpay.habitpay.global.security.WithMockOAuth2User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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
    static final String TIMEZONE = "Asia/Seoul";

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
    ChallengeDeleteService challengeDeleteService;

    @MockBean
    ChallengeRecordsService challengeRecordsService;

    @MockBean
    TimeZoneProperties timeZoneProperties;

    @MockBean
    TokenService tokenService;

    @MockBean
    TokenProvider tokenProvider;


    @Test
    @WithMockOAuth2User
    @DisplayName("챌린지 전체 목록 조회")
    void getChallengePage() throws Exception {

        // given
        ChallengePageResponse response = ChallengePageResponse.builder()
                .id(1L)
                .title("챌린지 제목")
                .startDate(ZonedDateTime.now())
                .endDate(ZonedDateTime.now().plusDays(5))
                .stopDate(null)
                .numberOfParticipants(1)
                .participatingDays(1 << 2)
                .isStarted(true)
                .isEnded(false)
                .hostNickname("챌린지 주최자 닉네임")
                .hostProfileImage("챌린지 주최자 프로필 이미지")
                .build();

        Page page = new PageImpl<>(List.of(response));

        given(challengeSearchService.getChallengePage(any(Pageable.class)))
                .willReturn(SuccessResponse.of(SuccessCode.NO_MESSAGE, PageResponse.from(page)));

        // when
        ResultActions result = mockMvc.perform(get("/api/challenges")
                .param("page", "page-number")
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN"));

        // then
        result.andExpect(status().isOk())
                .andDo(document("challenge/get-challenge-page",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data.content[].id").description("챌린지 ID"),
                                fieldWithPath("data.content[].title").description("챌린지 제목"),
                                fieldWithPath("data.content[].startDate").description("챌린지 시작 일시"),
                                fieldWithPath("data.content[].endDate").description("챌린지 종료 일시"),
                                fieldWithPath("data.content[].stopDate").description("챌린지 중단 일시"),
                                fieldWithPath("data.content[].numberOfParticipants").description("챌린지 참여자 수"),
                                fieldWithPath("data.content[].participatingDays").description("챌린지 총 진행 일"),
                                fieldWithPath("data.content[].isStarted").description("챌린지 시작 여부"),
                                fieldWithPath("data.content[].isEnded").description("챌린지 종료 여부"),
                                fieldWithPath("data.content[].hostNickname").description("챌린지 주최자 닉네임"),
                                fieldWithPath("data.content[].hostProfileImage").description("챌린지 주최자 프로필 이미지"),
                                fieldWithPath("data.page").description("현재 페이지 번호"),
                                fieldWithPath("data.size").description("현재 페이지 조회 결과 건수"),
                                fieldWithPath("data.totalElements").description("전체 페이지 조회 결과 건수"),
                                fieldWithPath("data.totalPages").description("전체 페이지 수"),
                                fieldWithPath("data.hasNextPage").description("다음 페이지 존재 유무")
                        )));
    }


    @Test
    @WithMockOAuth2User
    @DisplayName("나의 챌린지 참여 목록 조회")
    void getEnrolledChallengeList() throws Exception {

        // given
        ChallengeEnrolledListItemResponse response = ChallengeEnrolledListItemResponse.builder()
                .challengeId(1L)
                .title("챌린지 제목")
                .description("챌린지 설명")
                .startDate(ZonedDateTime.now())
                .endDate(ZonedDateTime.now().plusDays(5))
                .stopDate(null)
                .totalParticipatingDaysCount(2)
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
                .willReturn(SuccessResponse.of(SuccessCode.NO_MESSAGE, List.of(response)));

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
                                fieldWithPath("data[].challengeId").description("챌린지 ID"),
                                fieldWithPath("data[].title").description("챌린지 제목"),
                                fieldWithPath("data[].description").description("챌린지 설명"),
                                fieldWithPath("data[].startDate").description("챌린지 시작 일시"),
                                fieldWithPath("data[].endDate").description("챌린지 종료 일시"),
                                fieldWithPath("data[].stopDate").description("챌린지 중단 일시"),
                                fieldWithPath("data[].totalParticipatingDaysCount").description("챌린지 총 참여 일수"),
                                fieldWithPath("data[].numberOfParticipants").description("챌린지 참여 인원"),
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
                .totalAbsenceFee(0)
                .isPaidAll(false)
                .hostNickname("챌린지 주최자 닉네임")
                .enrolledMembersProfileImageList(List.of("imageLink1", "imageLink2", "imageLink3"))
                .isHost(true)
                .isMemberEnrolledInChallenge(true)
                .isTodayParticipatingDay(true)
                .isParticipatedToday(true)
                .build();

        given(challengeDetailsService.getChallengeDetails(anyLong(), any(Member.class)))
                .willReturn(SuccessResponse.of(SuccessCode.NO_MESSAGE, challengeDetailsResponse));

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
                                fieldWithPath("data.totalAbsenceFee").description("챌린지 전체 벌금"),
                                fieldWithPath("data.isPaidAll").description("최종 정산 여부"),
                                fieldWithPath("data.hostNickname").description("챌린지 주최자 닉네임"),
                                fieldWithPath("data.enrolledMembersProfileImageList").description("챌린지 참여자 프로필 이미지 (최대 3명)"),
                                fieldWithPath("data.isHost").description("현재 접속한 사용자 == 챌린지 주최자"),
                                fieldWithPath("data.isMemberEnrolledInChallenge").description("현재 접속한 사용자의 챌린지 참여 여부"),
                                fieldWithPath("data.isTodayParticipatingDay").description("금일이 챌린지 참여일인지 여부"),
                                fieldWithPath("data.isParticipatedToday").description("현재 접속한 사용자가 챌린지의 참가자일 경우, 금일 참여했는지 여부(참가자가 아니어도 false)")
                        )
                ));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("챌린지 상세 정보 조회 예외처리 - 존재하지 않는 챌린지 (404 Not Found)")
    void getChallengeDetailsNotFoundException() throws Exception {

        // given
        given(challengeDetailsService.getChallengeDetails(anyLong(), any(Member.class)))
                .willThrow(new ChallengeNotFoundException(0L));

        // when
        ResultActions result = mockMvc.perform(get("/api/challenges/{id}", 0L)
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN"));

        // then
        result.andExpect(status().isNotFound())
                .andDo(document("challenge/get-challenge-details-not-found-exception",
                        responseFields(
                                fieldWithPath("code").description("오류 응답 코드"),
                                fieldWithPath("message").description("오류 메세지")
                        )
                ));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("챌린지 별 참여 기록 조회")
    void getChallengeRecords() throws Exception {

        // given
        LocalDate today = LocalDate.now();
        ChallengeRecordsResponse challengeRecordsResponse = ChallengeRecordsResponse.builder()
                .successDayList(List.of(today))
                .failureDayList(new ArrayList<>())
                .upcomingDayList(List.of(today.plusWeeks(1)))
                .build();

        given(challengeRecordsService.getChallengeRecords(anyLong(), any(Member.class)))
                .willReturn(SuccessResponse.of(SuccessCode.NO_MESSAGE, challengeRecordsResponse));

        // when
        ResultActions result = mockMvc.perform(get("/api/challenges/{id}/records", 1L)
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN"));

        // then
        result.andExpect(status().isOk())
                .andDo(document("challenge/get-challenge-records",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data.successDayList").description("특정 챌린지 참여에 성공한 날짜 리스트"),
                                fieldWithPath("data.failureDayList").description("특정 챌린지 참여에 실패한 날짜 리스트"),
                                fieldWithPath("data.upcomingDayList").description("특정 챌린지 참여가 예정되어 있는 날짜 리스트")
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
                .willReturn(SuccessResponse.of(SuccessCode.CREATE_CHALLENGE_SUCCESS, challengeCreationResponse));

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
    @DisplayName("챌린지 생성 예외처리 - 챌린지 시작 시간 유효성 검증 (400 Bad Request)")
    void createChallengeInvalidStartTime() throws Exception {

        // given
        ZonedDateTime yesterday = ZonedDateTime.now().minusDays(1);
        ChallengeCreationRequest challengeCreationRequest = ChallengeCreationRequest.builder()
                .title("챌린지 제목")
                .description("챌린지 설명")
                .startDate(yesterday)
                .endDate(ZonedDateTime.now().plusDays(5))
                .participatingDays((byte) (1 << 2))
                .feePerAbsence(1000)
                .build();

        given(challengeCreationService.createChallenge(any(ChallengeCreationRequest.class), any(Member.class)))
                .willThrow(new ChallengeStartTimeInvalidException(yesterday));

        // when
        ResultActions result = mockMvc.perform(post("/api/challenges")
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN")
                .content(objectMapper.writeValueAsString(challengeCreationRequest))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isBadRequest())
                .andDo(document("challenge/create-challenge-invalid-start-time",
                        responseFields(
                                fieldWithPath("code").description("오류 응답 코드"),
                                fieldWithPath("message").description("오류 메세지")
                        )
                ));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("챌린지 생성 예외처리 - 챌린지 참여 요일 유효성 검증 (400 Bad Request)")
    void createChallengeInvalidParticipatingDays() throws Exception {

        // given
        given(timeZoneProperties.getTimeZone()).willReturn(TIMEZONE);
        ZonedDateTime startDate = ZonedDateTime.of(2024, 10, 7, 0, 0, 0, 0, ZoneId.of(timeZoneProperties.getTimeZone()));
        ChallengeCreationRequest challengeCreationRequest = ChallengeCreationRequest.builder()
                .title("챌린지 제목")
                .description("챌린지 설명")
                .startDate(startDate)
                .endDate(startDate.plusDays(1))
                .participatingDays((byte) (1 << 4))
                .feePerAbsence(1000)
                .build();

        given(challengeCreationService.createChallenge(any(ChallengeCreationRequest.class), any(Member.class)))
                .willThrow(new InvalidChallengeParticipatingDaysException());

        // when
        ResultActions result = mockMvc.perform(post("/api/challenges")
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN")
                .content(objectMapper.writeValueAsString(challengeCreationRequest))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isBadRequest())
                .andDo(document("challenge/create-challenge-invalid-participating-days",
                        responseFields(
                                fieldWithPath("code").description("오류 응답 코드"),
                                fieldWithPath("message").description("오류 메세지")
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

        given(challengePatchService.patch(anyLong(), any(ChallengePatchRequest.class), any(Member.class)))
                .willReturn(SuccessResponse.of(SuccessCode.PATCH_CHALLENGE_SUCCESS, challengePatchResponse));

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


    @Test
    @WithMockOAuth2User
    @DisplayName("챌린지 정보 수정 예외처리 - 챌린지 설명이 이전과 동일한 경우 (400 Bad Request)")
    void patchChallengeDuplicatedDescriptionException() throws Exception {

        // given
        ChallengePatchRequest challengePatchRequest = ChallengePatchRequest.builder()
                .description("챌린지 설명")
                .build();

        given(challengePatchService.patch(anyLong(), any(ChallengePatchRequest.class), any(Member.class)))
                .willThrow(new InvalidValueException(ErrorCode.DUPLICATED_CHALLENGE_DESCRIPTION));

        // when
        ResultActions result = mockMvc.perform(patch("/api/challenges/{id}", 1L)
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN")
                .content(objectMapper.writeValueAsString(challengePatchRequest))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isBadRequest())
                .andDo(document("challenge/patch-challenge-duplicated-description-exception",
                        responseFields(
                                fieldWithPath("code").description("오류 응답 코드"),
                                fieldWithPath("message").description("오류 메세지")
                        )
                ));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("챌린지 정보 수정 예외처리 - 챌린지 주최자가 아닌 경우 (403 Forbidden)")
    void patchChallengeForbiddenException() throws Exception {

        // given
        ChallengePatchRequest challengePatchRequest = ChallengePatchRequest.builder()
                .description("챌린지 설명")
                .build();

        given(challengePatchService.patch(anyLong(), any(ChallengePatchRequest.class), any(Member.class)))
                .willThrow(new ForbiddenException(ErrorCode.ONLY_HOST_CAN_MODIFY));

        // when
        ResultActions result = mockMvc.perform(patch("/api/challenges/{id}", 1L)
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN")
                .content(objectMapper.writeValueAsString(challengePatchRequest))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isForbidden())
                .andDo(document("challenge/patch-challenge-forbidden-exception",
                        responseFields(
                                fieldWithPath("code").description("오류 응답 코드"),
                                fieldWithPath("message").description("오류 메세지")
                        )
                ));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("챌린지 삭제")
    void deleteChallenge() throws Exception {

        // given
        given(challengeDeleteService.delete(anyLong(), anyLong()))
                .willReturn(SuccessResponse.of(SuccessCode.DELETE_CHALLENGE_SUCCESS));

        // when
        ResultActions result = mockMvc.perform(delete("/api/challenges/{id}", 1L)
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN"));

        // then
        result.andExpect(status().isOk())
                .andDo(document("challenge/delete",
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
    @DisplayName("챌린지 삭제 - 챌린지 주최자가 아닌 경우 (403 Forbidden)")
    void deleteChallengeForbiddenException() throws Exception {

        // given
        given(challengeDeleteService.delete(anyLong(), anyLong()))
                .willThrow(new ForbiddenException(ErrorCode.NOT_ALLOWED_TO_DELETE_CHALLENGE));

        // when
        ResultActions result = mockMvc.perform(delete("/api/challenges/{id}", 1L)
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN"));

        // then
        result.andExpect(status().isForbidden())
                .andDo(document("challenge/delete-forbidden-exception",
                        responseFields(
                                fieldWithPath("code").description("오류 응답 코드"),
                                fieldWithPath("message").description("오류 메세지")
                        )
                ));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("챌린지 삭제 - 존재하지 않는 챌린지 (404 Not Found)")
    void deleteChallengeNotFoundException() throws Exception {

        // given
        given(challengeDeleteService.delete(anyLong(), anyLong()))
                .willThrow(new ChallengeNotFoundException(0L));

        // when
        ResultActions result = mockMvc.perform(delete("/api/challenges/{id}", 0L)
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN"));

        // then
        result.andExpect(status().isNotFound())
                .andDo(document("challenge/delete-not-found-exception",
                        responseFields(
                                fieldWithPath("code").description("오류 응답 코드"),
                                fieldWithPath("message").description("오류 메세지")
                        )
                ));
    }
}
