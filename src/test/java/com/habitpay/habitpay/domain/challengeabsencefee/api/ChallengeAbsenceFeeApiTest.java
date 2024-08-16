package com.habitpay.habitpay.domain.challengeabsencefee.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habitpay.habitpay.docs.springrestdocs.AbstractRestDocsTests;
import com.habitpay.habitpay.domain.challengeabsencefee.application.ChallengeAbsenceFeeSearchService;
import com.habitpay.habitpay.domain.challengeabsencefee.dto.FeeStatusResponse;
import com.habitpay.habitpay.domain.challengeabsencefee.dto.MemberFeeResponse;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.global.config.jwt.TokenProvider;
import com.habitpay.habitpay.global.config.jwt.TokenService;
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

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChallengeAbsenceFeeApi.class)
public class ChallengeAbsenceFeeApiTest extends AbstractRestDocsTests {

    static final String AUTHORIZATION_HEADER_PREFIX = "Bearer ";

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ChallengeAbsenceFeeSearchService challengeAbsenceFeeSearchService;

    @MockBean
    TokenProvider tokenProvider;

    @MockBean
    TokenService tokenService;

    @Test
    @WithMockOAuth2User
    @DisplayName("챌린지 내 벌금 현황 조회")
    void getFeeStatusByChallenge() throws Exception {

        // given
        FeeStatusResponse feeStatusResponse = FeeStatusResponse.builder()
                .totalFee(1000)
                .myFee(0)
                .memberFeeList(List.of(
                        new MemberFeeResponse("testUser", 1000, 10),
                        new MemberFeeResponse("selfUser", 0, 20)))
                .build();

        given(challengeAbsenceFeeSearchService.makeMemberFeeDataListOfChallenge(any(Long.class), any(Member.class)))
                .willReturn(SuccessResponse.of(SuccessCode.NO_MESSAGE, feeStatusResponse));

        //when
        ResultActions result = mockMvc.perform(get("/api/challenges/{id}/fee", 1L)
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN"));

        //then
        result.andExpect(status().isOk())
                .andDo(document("challengeAbsenceFee/get-fee-status-by-challenge",
                        responseFields(
                                fieldWithPath("message").description("메시지"),
                                fieldWithPath("data.totalFee").description("챌린지 내 전체 누적 벌금 총합"),
                                fieldWithPath("data.myFee").description("챌린지 내 나의 누적 벌금 총합"),
                                fieldWithPath("data.memberFeeList").description("챌린지 내 멤버별 벌금 현황 목록"),
                                fieldWithPath("data.memberFeeList[].nickname").description("멤버 닉네임"),
                                fieldWithPath("data.memberFeeList[].totalFee").description("챌린지 내 멤버의 누적 벌금 총합"),
                                fieldWithPath("data.memberFeeList[].completionRate").description("챌린지 내 멤버의 달성률")
                        )
                ));
    }
}
