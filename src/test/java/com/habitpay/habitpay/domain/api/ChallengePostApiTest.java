package com.habitpay.habitpay.domain.api;

import com.habitpay.habitpay.docs.springrestdocs.AbstractRestDocsTests;
import com.habitpay.habitpay.domain.challenge.dao.ChallengeRepository;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.dao.ChallengeEnrollmentRepository;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengepost.api.ChallengePostApi;
import com.habitpay.habitpay.domain.challengepost.application.*;
import com.habitpay.habitpay.domain.challengepost.dao.ChallengePostRepository;
import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.challengepost.dto.PostViewResponse;
import com.habitpay.habitpay.domain.member.dao.MemberRepository;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.member.domain.Role;
import com.habitpay.habitpay.domain.model.BaseTime;
import com.habitpay.habitpay.domain.postphoto.dao.PostPhotoRepository;
import com.habitpay.habitpay.domain.postphoto.domain.PostPhoto;
import com.habitpay.habitpay.domain.postphoto.dto.PostPhotoView;
import com.habitpay.habitpay.global.config.jwt.TokenProvider;
import com.habitpay.habitpay.global.config.jwt.TokenService;
import com.habitpay.habitpay.global.security.WithMockOAuth2User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Date.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


//@WebMvcTest(ChallengePostApi.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ChallengePostApiTest extends AbstractRestDocsTests {

    static final String AUTHORIZATION_HEADER_PREFIX = "Bearer ";

    @MockBean
    ChallengePostSearchService challengePostSearchService;

    @MockBean
    ChallengePostUpdateService challengePostUpdateService;

    @MockBean
    ChallengePostDeleteService challengePostDeleteService;

    @MockBean
    ChallengePostCreationService challengePostCreationService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ChallengeRepository challengeRepository;

    @Autowired
    ChallengeEnrollmentRepository challengeEnrollmentRepository;

    @Autowired
    ChallengePostRepository challengePostRepository;

    @Autowired
    PostPhotoRepository postPhotoRepository;

    @MockBean
    TokenProvider tokenProvider;

    @MockBean
    TokenService tokenService;

    private Member createTestMember() {
        return memberRepository.save(Member.builder()
                .email("test@gmail.com")
                .role(Role.GUEST)
                .imageFileName(null)
                .nickname("test member")
                .build());
    }

    private Challenge createTestChallenge(Member testMember) {
        return challengeRepository.save(Challenge.builder()
                .member(testMember)
                .title("making test code")
                .description("making test code for restDocs")
                .startDate(ZonedDateTime.now())
                .endDate(ZonedDateTime.now())
                .feePerAbsence(1000)
                .build());
    }

    private ChallengeEnrollment createTestChallengeEnrollment(Member testMember, Challenge testChallenge) {
        return challengeEnrollmentRepository.save(ChallengeEnrollment.builder()
                .challenge(testChallenge)
                .member(testMember)
                .enrolledDate(ZonedDateTime.now())
                .build());
    }

    private ChallengePost createTestChallengePost(ChallengeEnrollment testEnrollment, String content) {
        return challengePostRepository.save(ChallengePost.builder()
                .content(content)
                .isAnnouncement(false)
                .enrollment(testEnrollment)
                .build());
    }

    private List<ChallengePost> createTestChallengePostList(ChallengeEnrollment testEnrollment, int i) {
        List<ChallengePost> postList = IntStream.rangeClosed(1, i)
                .mapToObj(index -> challengePostRepository.save(ChallengePost.builder()
                        .content("This is test post" + index)
                            .isAnnouncement(false)
                            .enrollment(testEnrollment)
                            .build()))
                .toList();

        return postList;
    }

    private List<PostPhotoView> createTestPostPhotoViewList(ChallengePost post) {
        PostPhoto photo = postPhotoRepository.save(PostPhoto.builder()
                .post(post)
                .viewOrder(1L)
                //.imageFileName(eq("https://picsum.photos/id/40/200/300"))
                .build());

        List<PostPhotoView> photoViewList = new ArrayList<>();
        photoViewList.add(new PostPhotoView(photo.getId(), photo.getViewOrder(), "https://picsum.photos/id/40/200/300"));

        return photoViewList;
    }

    @Test
    @DisplayName("챌린지 포스트 조회")
    void findPost() throws Exception {

        // given
        Member testMember = createTestMember();
        Challenge testChallenge = createTestChallenge(testMember);
        ChallengeEnrollment testEnrollment = createTestChallengeEnrollment(testMember, testChallenge);
        ChallengePost testPost = createTestChallengePost(testEnrollment, "This is test post");
        List<PostPhotoView> mockPostPhotoViewList = createTestPostPhotoViewList(testPost);

        // todo: API 리턴 타입을 SuccessResponse로 바꾸고 수정하기
        PostViewResponse mockPostViewResponse = new PostViewResponse(testPost, mockPostPhotoViewList);

        given(challengePostSearchService.findPostById(1L)).willReturn(mockPostViewResponse);

        // when
        // Mock을 통해 실행한 요청의 결과 (체이닝 방식?)
        ResultActions result = mockMvc.perform(get("/api/posts/{id}", 1L)
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN"));

        //then
        result.andExpect(status().isOk())
                //.andExpect(jsonPath("$.id").value(1L))
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
                                fieldWithPath("id").description("포스트 id(삭제 예정)"),
                                fieldWithPath("challengeEnrollmentId").description("포스트가 소속된 enrollment id(삭제 예정)"),
                                fieldWithPath("content").description("포스트 내용"),
                                fieldWithPath("writer").description("작성자"),
                                fieldWithPath("isAnnouncement").description("공지글 여부"),
                                fieldWithPath("createdAt").description("생성 일시, yyyy-MM-dd'T'HH:mm:ss 패턴 사용"),
                                fieldWithPath("photoViewList").description("포스트 포토(URL 포함) 데이터를 담은 객체 배열"),
                                fieldWithPath("photoViewList[].postPhotoId").description("포토 id(삭제 예정)"),
                                fieldWithPath("photoViewList[].viewOrder").description("포스트 내 포토의 순서"),
                                fieldWithPath("photoViewList[].imageUrl").description("포스트 포토 url")
                        )
                ));
    }

    @Test
    @DisplayName("챌린지 내 전체 포스트 조회")
    void findChallengePosts() throws Exception {

        // given
        Member testMember = createTestMember();
        Challenge testChallenge = createTestChallenge(testMember);
        ChallengeEnrollment testEnrollment = createTestChallengeEnrollment(testMember, testChallenge);

        int postCount = 3;
        List<ChallengePost> testPostList = createTestChallengePostList(testEnrollment, postCount);
        List<PostViewResponse> testPostViewResponseList = new ArrayList<>();
        // todo: 리턴 타입 SuccessResponse로 바꾸고 수정
        for (ChallengePost post : testPostList) {
            testPostViewResponseList.add(new PostViewResponse(post, createTestPostPhotoViewList(post)));
        }

        given(challengePostSearchService.findChallengePostsByChallengeId(anyLong())).willReturn(testPostViewResponseList);

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
                                fieldWithPath("[].createdAt").description("생성 일시, yyyy-MM-dd'T'HH:mm:ss 패턴 사용"),
                                fieldWithPath("[].photoViewList").description("포스트 포토(URL 포함) 데이터를 담은 객체 배열"),
                                fieldWithPath("[].photoViewList[].postPhotoId").description("포토 id(삭제 예정)"),
                                fieldWithPath("[].photoViewList[].viewOrder").description("포스트 내 포토의 순서"),
                                fieldWithPath("[].photoViewList[].imageUrl").description("포스트 포토 url")
                        )
                ));
    }

    // todo : given()에서 설정한 리턴 객체가 아닌 빈 배열이 오는 문제 -> 메서드에서 인자 수정하고 다시 시도
//    @Test
//    @DisplayName("챌린지 내 본인이 작성한 모든 포스트 조회")
//    void findChallengePostsByMe() throws Exception {
//
//        // given
//        Member testMember = createTestMember();
//        Challenge testChallenge = createTestChallenge(testMember);
//        ChallengeEnrollment testEnrollment = createTestChallengeEnrollment(testMember, testChallenge);
//        List<ChallengePost> mockPostList = createTestChallengePostList(testEnrollment);
//
//        List<PostPhotoView> mockPostPhotoViewList = null;
//        List<PostViewResponse> mockPostViewResponseList = new ArrayList<>();
//
//        // todo: 리턴 타입 SuccessResponse로 바꾸고 수정
//        for (ChallengePost mockPost : mockPostList) {
//            mockPostViewResponseList.add(new PostViewResponse(mockPost, mockPostPhotoViewList));
//        }
//        System.out.println(mockPostViewResponseList);
//
//        given(challengePostSearchService.findChallengePostsByMember(1L, "test@gmail.com")).willReturn(mockPostViewResponseList);
//
//        // when
//        // Mock을 통해 실행한 요청의 결과 (체이닝 방식?)
//        ResultActions result = mockMvc.perform(get("/api/challenges/{id}/posts/me", 1L)
//                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN"));
//
//        //then
//        result.andExpect(status().isOk())
////                .andExpect(jsonPath("$[0].id").value(1L))
//                .andDo(document("challengePost/get-challengePosts-by-me",
//                        requestHeaders(
//                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
//                        ),
//                        responseFields(
//                                // todo: API 리턴 타입을 SuccessResponse로 바꾸고 수정하기
//                                fieldWithPath("[].id").description("포스트 id"),
//                                fieldWithPath("[].challengeEnrollmentId").description("포스트가 소속된 enrollment id"),
//                                fieldWithPath("[].content").description("포스트 내용"),
//                                fieldWithPath("[].writer").description("작성자"),
//                                fieldWithPath("[].isAnnouncement").description("공지글 여부"),
//                                fieldWithPath("[].createdAt").description("생성 일시"),
//                                fieldWithPath("[].photoViewList").description("포스트 포토 URL을 담은 배열")
//                        )
//                ));
//
//    }

}