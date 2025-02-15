package com.habitpay.habitpay.domain.challegepost.api;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habitpay.habitpay.docs.springrestdocs.AbstractRestDocsTests;
import com.habitpay.habitpay.domain.challengepost.api.ChallengePostApi;
import com.habitpay.habitpay.domain.challengepost.application.ChallengePostCreationService;
import com.habitpay.habitpay.domain.challengepost.application.ChallengePostDeleteService;
import com.habitpay.habitpay.domain.challengepost.application.ChallengePostSearchService;
import com.habitpay.habitpay.domain.challengepost.application.ChallengePostUpdateService;
import com.habitpay.habitpay.domain.challengepost.application.ChallengePostUtilService;
import com.habitpay.habitpay.domain.challengepost.dto.AddPostRequest;
import com.habitpay.habitpay.domain.challengepost.dto.ModifyPostRequest;
import com.habitpay.habitpay.domain.challengepost.dto.PostViewResponse;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.postphoto.dto.AddPostPhotoData;
import com.habitpay.habitpay.domain.postphoto.dto.ModifyPostPhotoData;
import com.habitpay.habitpay.domain.postphoto.dto.PostPhotoView;
import com.habitpay.habitpay.global.config.jwt.TokenProvider;
import com.habitpay.habitpay.global.config.jwt.TokenService;
import com.habitpay.habitpay.global.error.exception.ErrorCode;
import com.habitpay.habitpay.global.error.exception.ForbiddenException;
import com.habitpay.habitpay.global.response.SliceResponse;
import com.habitpay.habitpay.global.response.SuccessCode;
import com.habitpay.habitpay.global.response.SuccessResponse;
import com.habitpay.habitpay.global.security.WithMockOAuth2User;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(ChallengePostApi.class)
public class ChallengePostApiTest extends AbstractRestDocsTests {

    static final String AUTHORIZATION_HEADER_PREFIX = "Bearer ";

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ChallengePostSearchService challengePostSearchService;

    @MockBean
    ChallengePostUpdateService challengePostUpdateService;

    @MockBean
    ChallengePostDeleteService challengePostDeleteService;

    @MockBean
    ChallengePostCreationService challengePostCreationService;

    @MockBean
    ChallengePostUtilService challengePostUtilService;

    @MockBean
    TokenProvider tokenProvider;

    @MockBean
    TokenService tokenService;


    @Test
    @WithMockOAuth2User
    @DisplayName("챌린지 포스트 조회")
    void findPost() throws Exception {

        // given
        PostViewResponse mockPostViewResponse = PostViewResponse.builder()
            .id(1L)
            .challengeId(1L)
            .content("This is test post.")
            .writer("test user")
            .isPostAuthor(true)
            .profileUrl("https://picsum.photos/id/40/200/300")
            .isAnnouncement(false)
            .createdAt(LocalDateTime.now())
            .photoViewList(List.of(
                new PostPhotoView(1L, 1L, "https://picsum.photos/id/40/200/300"),
                new PostPhotoView(2L, 2L, "https://picsum.photos/id/40/200/300")))
            .build();

        given(challengePostSearchService.getPostViewByPostId(anyLong(), any(Member.class)))
            .willReturn(SuccessResponse.of(SuccessCode.NO_MESSAGE, mockPostViewResponse));

        // when
        ResultActions result = mockMvc.perform(get("/api/posts/{id}", 1L)
            .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN"));

        //then
        result.andExpect(status().isOk())
            .andDo(document("challengePost/get-challenge-post",
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                ),
                responseFields(
                    fieldWithPath("message").description("응답 메시지"),
                    fieldWithPath("data").description("응답 데이터"),

                    fieldWithPath("data.id").description("포스트 id"),
                    fieldWithPath("data.challengeId").description("포스트가 소속된 challenge id"),
                    fieldWithPath("data.content").description("포스트 내용"),
                    fieldWithPath("data.writer").description("작성자"),
                    fieldWithPath("data.isPostAuthor").description("요청한 멤버가 작성자 본인인지 여부"),
                    fieldWithPath("data.profileUrl").description("작성자 프로필 이미지 URL"),
                    fieldWithPath("data.isAnnouncement").description("공지글 여부"),
                    fieldWithPath("data.createdAt").description("생성 일시"),
                    fieldWithPath("data.photoViewList").description("포스트 포토(URL 포함) 데이터를 담은 객체 배열"),
                    fieldWithPath("data.photoViewList[].postPhotoId").description("포토 id"),
                    fieldWithPath("data.photoViewList[].viewOrder").description("포스트 내 포토의 순서"),
                    fieldWithPath("data.photoViewList[].imageUrl").description("포스트 포토 url")
                )
            ));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("챌린지 내 전체 포스트 조회")
    void findChallengePosts() throws Exception {

        // given
        List<PostViewResponse> mockPostViewResponseList = List.of(
            PostViewResponse.builder()
                .id(1L)
                .challengeId(1L)
                .content("This is test post 1.")
                .writer("test user")
                .isPostAuthor(true)
                .profileUrl("")
                .isAnnouncement(false)
                .createdAt(LocalDateTime.now())
                .photoViewList(
                    List.of(new PostPhotoView(1L, 1L, "https://picsum.photos/id/40/200/300")))
                .build(),
            PostViewResponse.builder()
                .id(2L)
                .challengeId(2L)
                .content("This is test post 2.")
                .writer("test user2")
                .isPostAuthor(false)
                .profileUrl("https://picsum.photos/id/40/200/300")
                .isAnnouncement(false)
                .createdAt(LocalDateTime.now())
                .photoViewList(
                    List.of(new PostPhotoView(2L, 2L, "https://picsum.photos/id/40/200/300")))
                .build());

        Slice<PostViewResponse> mockPostViewResponseSlice = new SliceImpl<>(
            mockPostViewResponseList, PageRequest.of(0, 10), true
        );

        given(challengePostSearchService.findPostViewListByChallengeId(anyLong(), any(Member.class),
            any(Pageable.class)))
            .willReturn(SuccessResponse.of(SuccessCode.NO_MESSAGE,
                SliceResponse.from(mockPostViewResponseSlice)
            ));

        // when
        ResultActions result = mockMvc.perform(get("/api/challenges/{id}/posts", 1L)
            .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN"));

        //then
        result.andExpect(status().isOk())
            .andDo(document("challengePost/get-challenge-posts",
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                ),
                responseFields(
                    fieldWithPath("message").description("응답 메시지"),
                    fieldWithPath("data").description("응답 데이터"),

                    fieldWithPath("data.content").description("포스트 뷰 목록"),
                    fieldWithPath("data.content[].id").description("포스트 id"),
                    fieldWithPath("data.content[].challengeId").description(
                        "포스트가 소속된 challenge id"),
                    fieldWithPath("data.content[].content").description("포스트 내용"),
                    fieldWithPath("data.content[].writer").description("작성자"),
                    fieldWithPath("data.content[].isPostAuthor").description("요청한 멤버가 작성자 본인인지 여부"),
                    fieldWithPath("data.content[].profileUrl").description("작성자 프로필 이미지 URL"),
                    fieldWithPath("data.content[].isAnnouncement").description("공지글 여부"),
                    fieldWithPath("data.content[].createdAt").description("생성 일시"),
                    fieldWithPath("data.content[].photoViewList").description(
                        "포스트 포토(URL 포함) 데이터를 담은 객체 배열"),
                    fieldWithPath("data.content[].photoViewList[].postPhotoId").description(
                        "포토 id"),
                    fieldWithPath("data.content[].photoViewList[].viewOrder").description(
                        "포스트 내 포토의 순서"),
                    fieldWithPath("data.content[].photoViewList[].imageUrl").description(
                        "포스트 포토 url"),

                    fieldWithPath("data.pageNumber").description("현재 페이지 번호"),
                    fieldWithPath("data.size").description("현재 페이지의 크기"),
                    fieldWithPath("data.isFirst").description("이 페이지가 첫 번째 페이지인지 여부"),
                    fieldWithPath("data.isLast").description("이 페이지가 마지막 페이지인지 여부"),
                    fieldWithPath("data.isEmpty").description("페이지가 비어있는지 여부"),
                    fieldWithPath("data.hasNextPage").description("다음 페이지 존재 여부"),

                    fieldWithPath("data.pageable").description("포스트 뷰 페이지네이션 정보를 담은 Pageable 객체"),
                    fieldWithPath("data.pageable.pageNumber").description("현재 페이지 번호"),
                    fieldWithPath("data.pageable.pageSize").description("한 페이지에 포함되는 항목의 수"),
                    fieldWithPath("data.pageable.sort").description("정렬 정보"),
                    fieldWithPath("data.pageable.sort.empty").description("정렬 조건이 없는지 여부"),
                    fieldWithPath("data.pageable.sort.unsorted").description("정렬되지 않았는지 여부"),
                    fieldWithPath("data.pageable.sort.sorted").description("정렬되었는지 여부"),
                    fieldWithPath("data.pageable.offset").description("페이징된 항목의 시작 위치"),
                    fieldWithPath("data.pageable.paged").description("페이징된 요청인지 여부"),
                    fieldWithPath("data.pageable.unpaged").description("페이징되지 않은 요청인지 여부")
                )
            ));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("챌린지 내 전체 포스트 중 공지 포스트 조회")
    void findAnnouncementPosts() throws Exception {

        // given
        List<PostViewResponse> mockPostViewResponseList = List.of(
            PostViewResponse.builder()
                .id(1L)
                .challengeId(1L)
                .content("This is announcement test post 1.")
                .writer("test user")
                .isPostAuthor(false)
                .profileUrl("")
                .isAnnouncement(true)
                .createdAt(LocalDateTime.now())
                .photoViewList(
                    List.of(new PostPhotoView(1L, 1L, "https://picsum.photos/id/40/200/300")))
                .build(),
            PostViewResponse.builder()
                .id(2L)
                .challengeId(2L)
                .content("This is announcement test post 2.")
                .writer("test user")
                .isPostAuthor(false)
                .profileUrl("https://picsum.photos/id/40/200/300")
                .isAnnouncement(true)
                .createdAt(LocalDateTime.now())
                .photoViewList(
                    List.of(new PostPhotoView(2L, 2L, "https://picsum.photos/id/40/200/300")))
                .build());

        Slice<PostViewResponse> mockPostViewResponseSlice = new SliceImpl<>(
            mockPostViewResponseList, PageRequest.of(0, 10), true
        );

        given(challengePostSearchService.findAnnouncementPostViewListByChallengeId(anyLong(),
            any(Member.class), any(Pageable.class)))
            .willReturn(SuccessResponse.of(SuccessCode.NO_MESSAGE, SliceResponse.from(mockPostViewResponseSlice)));

        // when
        ResultActions result = mockMvc.perform(get("/api/challenges/{id}/posts/announcements", 1L)
            .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN"));

        //then
        result.andExpect(status().isOk())
            .andDo(document("challengePost/get-announcement-challenge-posts",
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                ),
                responseFields(
                    fieldWithPath("message").description("응답 메시지"),
                    fieldWithPath("data").description("응답 데이터"),

                    fieldWithPath("data.content").description("포스트 뷰 목록"),
                    fieldWithPath("data.content[].id").description("포스트 id"),
                    fieldWithPath("data.content[].challengeId").description(
                        "포스트가 소속된 challenge id"),
                    fieldWithPath("data.content[].content").description("포스트 내용"),
                    fieldWithPath("data.content[].writer").description("작성자"),
                    fieldWithPath("data.content[].isPostAuthor").description("요청한 멤버가 작성자 본인인지 여부"),
                    fieldWithPath("data.content[].profileUrl").description("작성자 프로필 이미지 URL"),
                    fieldWithPath("data.content[].isAnnouncement").description("공지글 여부"),
                    fieldWithPath("data.content[].createdAt").description("생성 일시"),
                    fieldWithPath("data.content[].photoViewList").description(
                        "포스트 포토(URL 포함) 데이터를 담은 객체 배열"),
                    fieldWithPath("data.content[].photoViewList[].postPhotoId").description(
                        "포토 id"),
                    fieldWithPath("data.content[].photoViewList[].viewOrder").description(
                        "포스트 내 포토의 순서"),
                    fieldWithPath("data.content[].photoViewList[].imageUrl").description(
                        "포스트 포토 url"),

                    fieldWithPath("data.pageNumber").description("현재 페이지 번호"),
                    fieldWithPath("data.size").description("현재 페이지의 크기"),
                    fieldWithPath("data.isFirst").description("이 페이지가 첫 번째 페이지인지 여부"),
                    fieldWithPath("data.isLast").description("이 페이지가 마지막 페이지인지 여부"),
                    fieldWithPath("data.isEmpty").description("페이지가 비어있는지 여부"),
                    fieldWithPath("data.hasNextPage").description("다음 페이지 존재 여부"),

                    fieldWithPath("data.pageable").description("포스트 뷰 페이지네이션 정보를 담은 Pageable 객체"),
                    fieldWithPath("data.pageable.pageNumber").description("현재 페이지 번호"),
                    fieldWithPath("data.pageable.pageSize").description("한 페이지에 포함되는 항목의 수"),
                    fieldWithPath("data.pageable.sort").description("정렬 정보"),
                    fieldWithPath("data.pageable.sort.empty").description("정렬 조건이 없는지 여부"),
                    fieldWithPath("data.pageable.sort.unsorted").description("정렬되지 않았는지 여부"),
                    fieldWithPath("data.pageable.sort.sorted").description("정렬되었는지 여부"),
                    fieldWithPath("data.pageable.offset").description("페이징된 항목의 시작 위치"),
                    fieldWithPath("data.pageable.paged").description("페이징된 요청인지 여부"),
                    fieldWithPath("data.pageable.unpaged").description("페이징되지 않은 요청인지 여부")
                )
            ));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("챌린지 내 본인이 작성한 모든 포스트 조회")
    void findChallengePostsByMe() throws Exception {

        // given
        List<PostViewResponse> mockPostViewResponseList = List.of(
            PostViewResponse.builder()
                .id(1L)
                .challengeId(1L)
                .content("This is test post 1 by me.")
                .writer("test user")
                .isPostAuthor(true)
                .profileUrl("")
                .isAnnouncement(false)
                .createdAt(LocalDateTime.now())
                .photoViewList(
                    List.of(new PostPhotoView(1L, 1L, "https://picsum.photos/id/40/200/300")))
                .build(),
            PostViewResponse.builder()
                .id(2L)
                .challengeId(2L)
                .content("This is test post 2 by me.")
                .writer("test user")
                .isPostAuthor(true)
                .profileUrl("https://picsum.photos/id/40/200/300")
                .isAnnouncement(false)
                .createdAt(LocalDateTime.now())
                .photoViewList(
                    List.of(new PostPhotoView(2L, 2L, "https://picsum.photos/id/40/200/300")))
                .build());

        Slice<PostViewResponse> mockPostViewResponseSlice = new SliceImpl<>(
            mockPostViewResponseList, PageRequest.of(0, 10), true
        );

        given(challengePostSearchService.findPostViewListByMember(anyLong(), any(Member.class),
            any(Pageable.class)))
            .willReturn(SuccessResponse.of(SuccessCode.NO_MESSAGE, SliceResponse.from(mockPostViewResponseSlice)));

        // when
        ResultActions result = mockMvc.perform(get("/api/challenges/{id}/posts/me", 1L)
            .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN"));

        //then
        result.andExpect(status().isOk())
            .andDo(document("challengePost/get-challenge-posts-by-me",
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                ),
                responseFields(
                    fieldWithPath("message").description("응답 메시지"),
                    fieldWithPath("data").description("응답 데이터"),

                    fieldWithPath("data.content").description("포스트 뷰 목록"),
                    fieldWithPath("data.content[].id").description("포스트 id"),
                    fieldWithPath("data.content[].challengeId").description(
                        "포스트가 소속된 challenge id"),
                    fieldWithPath("data.content[].content").description("포스트 내용"),
                    fieldWithPath("data.content[].writer").description("작성자"),
                    fieldWithPath("data.content[].isPostAuthor").description("요청한 멤버가 작성자 본인인지 여부"),
                    fieldWithPath("data.content[].profileUrl").description("작성자 프로필 이미지 URL"),
                    fieldWithPath("data.content[].isAnnouncement").description("공지글 여부"),
                    fieldWithPath("data.content[].createdAt").description("생성 일시"),
                    fieldWithPath("data.content[].photoViewList").description(
                        "포스트 포토(URL 포함) 데이터를 담은 객체 배열"),
                    fieldWithPath("data.content[].photoViewList[].postPhotoId").description(
                        "포토 id"),
                    fieldWithPath("data.content[].photoViewList[].viewOrder").description(
                        "포스트 내 포토의 순서"),
                    fieldWithPath("data.content[].photoViewList[].imageUrl").description(
                        "포스트 포토 url"),

                    fieldWithPath("data.pageNumber").description("현재 페이지 번호"),
                    fieldWithPath("data.size").description("현재 페이지의 크기"),
                    fieldWithPath("data.isFirst").description("이 페이지가 첫 번째 페이지인지 여부"),
                    fieldWithPath("data.isLast").description("이 페이지가 마지막 페이지인지 여부"),
                    fieldWithPath("data.isEmpty").description("페이지가 비어있는지 여부"),
                    fieldWithPath("data.hasNextPage").description("다음 페이지 존재 여부"),

                    fieldWithPath("data.pageable").description("포스트 뷰 페이지네이션 정보를 담은 Pageable 객체"),
                    fieldWithPath("data.pageable.pageNumber").description("현재 페이지 번호"),
                    fieldWithPath("data.pageable.pageSize").description("한 페이지에 포함되는 항목의 수"),
                    fieldWithPath("data.pageable.sort").description("정렬 정보"),
                    fieldWithPath("data.pageable.sort.empty").description("정렬 조건이 없는지 여부"),
                    fieldWithPath("data.pageable.sort.unsorted").description("정렬되지 않았는지 여부"),
                    fieldWithPath("data.pageable.sort.sorted").description("정렬되었는지 여부"),
                    fieldWithPath("data.pageable.offset").description("페이징된 항목의 시작 위치"),
                    fieldWithPath("data.pageable.paged").description("페이징된 요청인지 여부"),
                    fieldWithPath("data.pageable.unpaged").description("페이징되지 않은 요청인지 여부")
                )
            ));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("챌린지 게시물 생성")
    void createPost() throws Exception {

        //given
        AddPostRequest mockAddPostRequest = AddPostRequest.builder()
            .content("I want to create this post.")
            .isAnnouncement(false)
            .photos(List.of(new AddPostPhotoData(1L, "jpg", 100L)))
            .build();

        List<String> presignedUrlList = List.of("https://please.upload/your-photo/here");

        given(challengePostCreationService.createPost(any(AddPostRequest.class), anyLong(),
            any(Member.class)))
            .willReturn(SuccessResponse.of(SuccessCode.CREATE_POST_SUCCESS, presignedUrlList));

        //when
        ResultActions result = mockMvc.perform(post("/api/challenges/{id}/posts", 1L)
            .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN")
            .content(objectMapper.writeValueAsString(mockAddPostRequest))
            .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
            .andDo(document("challengePost/create-challenge-post",
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                ),
                requestFields(
                    fieldWithPath("content").description("포스트 내용"),
                    fieldWithPath("isAnnouncement").description("공지 포스트 여부"),
                    fieldWithPath("photos").description("첨부한 이미지 파일 목록"),
                    fieldWithPath("photos[].viewOrder").description("첨부한 이미지 파일의 포스트 내 정렬 순서"),
                    fieldWithPath("photos[].imageExtension").description("첨부한 이미지 파일의 확장자명"),
                    fieldWithPath("photos[].contentLength").description("첨부한 이미지 파일의 길이")
                ),
                responseFields(
                    fieldWithPath("message").description("메시지"),
                    fieldWithPath("data").description("AWS S3 업로드를 위한 preSignedUrl List<String>")
                )
            ));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("챌린지 게시물 생성 - 게시물 길이 초과 (400 Bad Request)")
    void challengePostCreationContentLengthTooLongException() throws Exception {

        // given
        AddPostRequest invalidRequest = AddPostRequest.builder()
            .content("A".repeat(1001)) // 본문 길이 초과
            .isAnnouncement(false)
            .photos(List.of(new AddPostPhotoData(1L, "jpg", 100L)))
            .build();

        // when
        ResultActions result = mockMvc.perform(post("/api/challenges/{id}/posts", 1L)
            .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN")
            .content(objectMapper.writeValueAsString(invalidRequest))
            .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("본문 길이는 최대 1000자 입니다."))
            .andDo(document("challenge/challenge-post-creation-content-length-too-long-exception",
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
    @DisplayName("챌린지 게시물 생성 - 챌린지 중도 포기 이후 (403 Forbidden)")
    void challengePostCreationForbiddenException() throws Exception {

        // given
        AddPostRequest mockAddPostRequest = AddPostRequest.builder()
            .content("I want to create this post.")
            .isAnnouncement(false)
            .photos(List.of(new AddPostPhotoData(1L, "jpg", 100L)))
            .build();

        given(challengePostCreationService.createPost(any(AddPostRequest.class), anyLong(),
            any(Member.class)))
            .willThrow(new ForbiddenException(ErrorCode.POST_CREATION_FORBIDDEN_DUE_TO_GIVE_UP));

        // when
        ResultActions result = mockMvc.perform(post("/api/challenges/{id}/posts", 1L)
            .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN")
            .content(objectMapper.writeValueAsString(mockAddPostRequest))
            .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isForbidden())
            .andDo(document("challenge/challenge-post-creation-forbidden-exception",
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
    @DisplayName("챌린지 포스트 수정")
    void patchPost() throws Exception {

        //given
        ModifyPostRequest mockmodifyPostRequest = ModifyPostRequest.builder()
            .content("I want to patch this to post.")
            .isAnnouncement(false)
            .newPhotos(List.of(new AddPostPhotoData(2L, "jpg", 100L)))
            .modifiedPhotos(List.of(new ModifyPostPhotoData(3L, 1L)))
            .deletedPhotoIds(List.of(1L))
            .build();

        List<String> presignedUrlList = List.of("https://please.upload/your-photo/here");

        given(
            challengePostUpdateService.patchPost(any(ModifyPostRequest.class), anyLong(), anyLong(),
                any(Member.class)))
            .willReturn(SuccessResponse.of(SuccessCode.PATCH_POST_SUCCESS, presignedUrlList));

        //when
        ResultActions result = mockMvc.perform(
            patch("/api/challenges/{challengeId}/posts/{postId}", 1L, 1L)
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN")
                .content(objectMapper.writeValueAsString(mockmodifyPostRequest))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
            .andDo(document("challengePost/patch-challenge-post",
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                ),
                requestFields(
                    fieldWithPath("content").description("포스트 수정 내용"),
                    fieldWithPath("isAnnouncement").description("공지 포스트 여부"),
                    fieldWithPath("newPhotos").description("새로 첨부한 이미지 파일 목록"),
                    fieldWithPath("newPhotos[].viewOrder").description("첨부한 이미지 파일의 포스트 내 정렬 순서"),
                    fieldWithPath("newPhotos[].imageExtension").description("첨부한 이미지 파일의 확장자명"),
                    fieldWithPath("newPhotos[].contentLength").description("첨부한 이미지 파일의 길이"),
                    fieldWithPath("modifiedPhotos").description("포스트 내 정렬 순서가 변경된 이미지 파일 목록"),
                    fieldWithPath("modifiedPhotos[].photoId").description(
                        "정렬 순서를 변경하려는 이미지 파일의 PostPhotoId"),
                    fieldWithPath("modifiedPhotos[].viewOrder").description("변경하려는 정렬 순서"),
                    fieldWithPath("deletedPhotoIds").description(
                        "삭제할 이미지 파일 PostPhotoId List<Long>")
                ),
                responseFields(
                    fieldWithPath("message").description("메시지"),
                    fieldWithPath("data").description("AWS S3 업로드를 위한 preSignedUrl List<String>")
                )
            ));
    }


    @Test
    @WithMockOAuth2User
    @DisplayName("챌린지 게시물 수정 - 게시물 길이 초과 (400 Bad Request)")
    void challengePostPatchContentLengthTooLongException() throws Exception {

        // given
        ModifyPostRequest invalidRequest = ModifyPostRequest.builder()
            .content("A".repeat(1001))
            .isAnnouncement(false)
            .newPhotos(List.of(new AddPostPhotoData(2L, "jpg", 100L)))
            .modifiedPhotos(List.of(new ModifyPostPhotoData(3L, 1L)))
            .deletedPhotoIds(List.of(1L))
            .build();

        // when
        ResultActions result = mockMvc.perform(
            patch("/api/challenges/{challengeId}/posts/{postId}", 1L, 1L)
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN")
                .content(objectMapper.writeValueAsString(invalidRequest))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("본문 길이는 최대 1000자 입니다."))
            .andDo(document("challenge/challenge-post-patch-content-length-too-long-exception",
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
    @DisplayName("챌린지 게시물 수정 - 챌린지 중도 포기 이후 (403 Forbidden)")
    void challengePostModificationForbiddenException() throws Exception {

        // given
        AddPostRequest mockPatchRequest = AddPostRequest.builder()
            .content("I want to create this post.")
            .isAnnouncement(false)
            .photos(List.of(new AddPostPhotoData(1L, "jpg", 100L)))
            .build();

        given(
            challengePostUpdateService.patchPost(any(ModifyPostRequest.class), anyLong(), anyLong(),
                any(Member.class)))
            .willThrow(
                new ForbiddenException(ErrorCode.POST_MODIFICATION_FORBIDDEN_DUE_TO_GIVE_UP));

        // when
        ResultActions result = mockMvc.perform(
            patch("/api/challenges/{challengeId}/posts/{postId}", 1L, 1L)
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN")
                .content(objectMapper.writeValueAsString(mockPatchRequest))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isForbidden())
            .andDo(document("challenge/challenge-post-modification-forbidden-exception",
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
    @DisplayName("챌린지 포스트 삭제")
    void deletePost() throws Exception {

        //given
        given(challengePostDeleteService.deletePost(anyLong(), anyLong(), any(Member.class)))
            .willReturn(SuccessResponse.of(SuccessCode.DELETE_POST_SUCCESS));

        //when
        ResultActions result = mockMvc.perform(
            delete("/api/challenges/{challengeId}/posts/{postId}", 1L, 1L)
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN"));

        //then
        result.andExpect(status().isOk())
            .andDo(document("challengePost/delete-challenge-post",
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                ),
                responseFields(
                    fieldWithPath("message").description("메시지"),
                    fieldWithPath("data").description("삭제된 포스트 id")
                )
            ));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("챌린지 게시물 삭제 - 챌린지 중도 포기 이후 (403 Forbidden)")
    void challengePostDeletionForbiddenException() throws Exception {

        // given
        given(challengePostDeleteService.deletePost(anyLong(), anyLong(), any(Member.class)))
            .willThrow(new ForbiddenException(ErrorCode.POST_DELETION_FORBIDDEN_DUE_TO_GIVE_UP));

        // when
        ResultActions result = mockMvc.perform(
            delete("/api/challenges/{challengeId}/posts/{postId}", 1L, 1L)
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN"));

        // then
        result.andExpect(status().isForbidden())
            .andDo(document("challenge/challenge-post-deletion-forbidden-exception",
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