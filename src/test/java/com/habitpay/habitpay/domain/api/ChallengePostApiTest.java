package com.habitpay.habitpay.domain.api;

import com.habitpay.habitpay.docs.springrestdocs.AbstractRestDocsTests;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengepost.api.ChallengePostApi;
import com.habitpay.habitpay.domain.challengepost.application.*;
import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.challengepost.dto.PostViewResponse;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.member.domain.Role;
import com.habitpay.habitpay.domain.postphoto.dto.PostPhotoView;
import com.habitpay.habitpay.global.config.jwt.TokenProvider;
import com.habitpay.habitpay.global.config.jwt.TokenService;
import com.habitpay.habitpay.global.security.WithMockOAuth2User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.util.Date.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChallengePostApi.class)
public class ChallengePostApiTest extends AbstractRestDocsTests {

    static final String AUTHORIZATION_HEADER_PREFIX = "Bearer ";

    // 해당 클래스가 의존하고 있는 객체는 @MockBean으로 가짜 객체 만들어주기
    @MockBean
    ChallengePostSearchService challengePostSearchService;

    @MockBean
    ChallengePostUpdateService challengePostUpdateService;

    @MockBean
    ChallengePostDeleteService challengePostDeleteService;

    @MockBean
    ChallengePostCreationService challengePostCreationService;

    @MockBean
    TokenProvider tokenProvider;

    @MockBean
    TokenService tokenService;

    private Member createTestMember() {
        return Member.builder()
                .id(1L)
                .email("test@gmail.com")
//                .role(Role.valueOf("ROLE_GUEST"))
                .imageFileName(null)
                .nickname("test user")
                .build();
    }

    private Challenge createTestChallenge(Member testMember) {
        return Challenge.builder()
                .member(testMember)
                .title("making test code")
                .description("making test code for restDocs")
                .startDate(ZonedDateTime.now())
                .endDate(ZonedDateTime.now())
                .feePerAbsence(1000)
                .build();
    }

    private ChallengeEnrollment createTestChallengeEnrollment(Member testMember, Challenge testChallenge) {
        return ChallengeEnrollment.builder()
                .challenge(testChallenge)
                .member(testMember)
                .enrolledDate(ZonedDateTime.now())
                .build();
    }

    // given.(의존관계에 있는 객체의 메서드(인자)).willReturn(반환값);

    @Test
    @DisplayName("챌린지 포스트 조회")
    void findPost() throws Exception {

        // given
        Member testMember = createTestMember();
        Challenge testChallenge = createTestChallenge(testMember);

        ChallengeEnrollment mockEnrollment = createTestChallengeEnrollment(testMember, testChallenge);

        ChallengePost mockPost = ChallengePost.builder()
                .content("This is test post.")
                .isAnnouncement(false)
                .enrollment(mockEnrollment)
                .build();

        List<PostPhotoView> mockPostPhotoViewList = null;

        // todo: API 리턴 타입을 SuccessResponse로 바꾸고 수정하기
        PostViewResponse mockPostViewResponse = new PostViewResponse(mockPost, mockPostPhotoViewList);

        given(challengePostSearchService.findPostById(anyLong())).willReturn(mockPostViewResponse);

        // when
        // Mock을 통해 실행한 요청의 결과 (체이닝 방식?)
        ResultActions result = mockMvc.perform(get("/api/posts/{id}", 1L)
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN"));

        //then
        result.andExpect(status().isOk())
                .andDo(document("challengePost/get-challengePost",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                        ),
                        responseFields(
                                // todo: API 리턴 타입을 SuccessResponse로 바꾸고 수정하기 (다른 테스트 메서드도!)
                                // fieldWithPath("message").description("메시지"),
//                                fieldWithPath("data.id").description("포스트 id"),
//                                fieldWithPath("data.challengeEnrollmentId").description("포스트가 소속된 enrollment id"),
//                                fieldWithPath("data.content").description("포스트 내용"),
//                                fieldWithPath("data.writer").description("작성자"),
//                                fieldWithPath("data.isAnnouncement").description("공지글 여부"),
//                                fieldWithPath("data.createdAt").description("생성 일시"),
//                                fieldWithPath("data.photoViewList").description("포스트 포토 URL을 담은 배열")
                                fieldWithPath("id").description("포스트 id"),
                                fieldWithPath("challengeEnrollmentId").description("포스트가 소속된 enrollment id"),
                                fieldWithPath("content").description("포스트 내용"),
                                fieldWithPath("writer").description("작성자"),
                                fieldWithPath("isAnnouncement").description("공지글 여부"),
                                fieldWithPath("createdAt").description("생성 일시"),
                                fieldWithPath("photoViewList").description("포스트 포토 URL을 담은 배열")
                        )
                ));
    }

    @Test
    @DisplayName("챌린지 내 전체 포스트 조회")
    void findChallengePosts() throws Exception {

        // given
        Member mockMember = Member.builder()
                .id(1L)
                .email("test@gmail.com")
//                .role(Role.valueOf("ROLE_GUEST"))
                .imageFileName(null)
                .nickname("testy user")
                .build();

        Challenge mockChallenge = Challenge.builder()
                .member(mockMember)
                .title("making test code")
                .description("making test code for restDocs")
                .startDate(ZonedDateTime.now())
                .endDate(ZonedDateTime.now())
                .feePerAbsence(1000)
                .build();

        ChallengeEnrollment mockEnrollment = ChallengeEnrollment.builder()
                .challenge(mockChallenge)
                .member(mockMember)
                .enrolledDate(ZonedDateTime.now())
                .build();

        List<ChallengePost> mockPostList = new ArrayList<ChallengePost>();

        int i = 0;
        while (i < 10) {
            mockPostList.add(
                    ChallengePost.builder()
                            .content("This is test post" + i)
                            .isAnnouncement(false)
                            .enrollment(mockEnrollment)
                            .build()
            );
            ++i;
        }

        List<PostPhotoView> mockPostPhotoViewList = null;
        List<PostViewResponse> mockPostViewResponseList = new ArrayList<PostViewResponse>();

        // todo: 리턴 타입 SuccessResponse로 바꾸고 수정
        for (ChallengePost mockPost : mockPostList) {
            mockPostViewResponseList.add(new PostViewResponse(mockPost, mockPostPhotoViewList));
        }

        given(challengePostSearchService.findChallengePostsByChallengeId(anyLong())).willReturn(mockPostViewResponseList);

        // when
        // Mock을 통해 실행한 요청의 결과 (체이닝 방식?)
        ResultActions result = mockMvc.perform(get("/api/challenges/{id}/posts", 1L)
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN"));

        //then
        result.andExpect(status().isOk())
                //.andExpect(jsonPath("$[0].id").value(1L))
                .andDo(document("challengePost/get-challengePosts",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                        ),
                        responseFields(
                                // todo: API 리턴 타입을 SuccessResponse로 바꾸고 수정하기
                                fieldWithPath("[].id").description("포스트 id"),
                                fieldWithPath("[].challengeEnrollmentId").description("포스트가 소속된 enrollment id"),
                                fieldWithPath("[].content").description("포스트 내용"),
                                fieldWithPath("[].writer").description("작성자"),
                                fieldWithPath("[].isAnnouncement").description("공지글 여부"),
                                fieldWithPath("[].createdAt").description("생성 일시"),
                                fieldWithPath("[].photoViewList").description("포스트 포토 URL을 담은 배열")
                        )
                ));
    }

}