package com.habitpay.habitpay.domain.challegepost.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habitpay.habitpay.docs.springrestdocs.AbstractRestDocsTests;
import com.habitpay.habitpay.domain.challengepost.api.ChallengePostApi;
import com.habitpay.habitpay.domain.challengepost.application.*;
import com.habitpay.habitpay.domain.challengepost.dto.AddPostRequest;
import com.habitpay.habitpay.domain.challengepost.dto.ModifyPostRequest;
import com.habitpay.habitpay.domain.challengepost.dto.PostViewResponse;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.postphoto.dto.AddPostPhotoData;
import com.habitpay.habitpay.domain.postphoto.dto.ModifyPostPhotoData;
import com.habitpay.habitpay.domain.postphoto.dto.PostPhotoView;
import com.habitpay.habitpay.global.config.jwt.TokenProvider;
import com.habitpay.habitpay.global.config.jwt.TokenService;
import com.habitpay.habitpay.global.response.SuccessResponse;
import com.habitpay.habitpay.global.security.WithMockOAuth2User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

//    private final Pageable pageable = PageRequest.of(0, 10);
    private Member createMockMember() {
        return Member.builder()
                .nickname("test member")
                .email("test_user@test.com")
                .build();
    }

//    private final List<String> presignedUrlList = List.of("https://please.upload/your-photo/here");

    @Test
    @DisplayName("챌린지 포스트 조회")
    void findPost() throws Exception {

        // given
        PostViewResponse mockPostViewResponse = PostViewResponse.builder()
                .id(1L)
                .challengeEnrollmentId(1L)
                .content("This is test post.")
                .writer("test user")
                .isAnnouncement(false)
                .createdAt(LocalDateTime.now())
                .photoViewList(List.of(new PostPhotoView(1L, 1L, "https://picsum.photos/id/40/200/300")))
                .build();

        given(challengePostSearchService.getPostViewResponseByPostId(anyLong()))
                .willReturn(SuccessResponse.of("", mockPostViewResponse));

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
                                fieldWithPath("message").description("메시지"),
                                fieldWithPath("data.id").description("포스트 id"),
                                fieldWithPath("data.challengeEnrollmentId").description("포스트가 소속된 enrollment id"),
                                fieldWithPath("data.content").description("포스트 내용"),
                                fieldWithPath("data.writer").description("작성자"),
                                fieldWithPath("data.isAnnouncement").description("공지글 여부"),
                                fieldWithPath("data.createdAt").description("생성 일시"),
                                fieldWithPath("data.photoViewList").description("포스트 포토(URL 포함) 데이터를 담은 객체 배열"),
                                fieldWithPath("data.photoViewList[].postPhotoId").description("포토 id(삭제 예정)"),
                                fieldWithPath("data.photoViewList[].viewOrder").description("포스트 내 포토의 순서"),
                                fieldWithPath("data.photoViewList[].imageUrl").description("포스트 포토 url")
                        )
                ));
    }

    @Test
    @DisplayName("챌린지 내 전체 포스트 조회")
    void findChallengePosts() throws Exception {

        // given
        List<PostViewResponse> mockPostViewResponseList = List.of(PostViewResponse.builder()
                .id(1L)
                .challengeEnrollmentId(1L)
                .content("This is test post for getting challenge posts.")
                .writer("test user")
                .isAnnouncement(false)
                .createdAt(LocalDateTime.now())
                .photoViewList(List.of(new PostPhotoView(1L, 1L, "https://picsum.photos/id/40/200/300")))
                .build());

        given(challengePostSearchService.findPostViewResponseListByChallengeId(anyLong(), any(Pageable.class)))
                .willReturn(SuccessResponse.of("", mockPostViewResponseList));

        // when
        ResultActions result = mockMvc.perform(get("/api/challenges/{id}/posts", 1L)
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", anyString())
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN"));

        //then
        result.andExpect(status().isOk())
                .andDo(document("challengePost/get-challenge-posts",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("message").description("메시지"),
                                fieldWithPath("data.[].id").description("포스트 id"),
                                fieldWithPath("data.[].challengeEnrollmentId").description("포스트가 소속된 enrollment id"),
                                fieldWithPath("data.[].content").description("포스트 내용"),
                                fieldWithPath("data.[].writer").description("작성자"),
                                fieldWithPath("data.[].isAnnouncement").description("공지글 여부"),
                                fieldWithPath("data.[].createdAt").description("생성 일시"),
                                fieldWithPath("data.[].photoViewList").description("포스트 포토(URL 포함) 데이터를 담은 객체 배열"),
                                fieldWithPath("data.[].photoViewList[].postPhotoId").description("포토 id(삭제 예정)"),
                                fieldWithPath("data.[].photoViewList[].viewOrder").description("포스트 내 포토의 순서"),
                                fieldWithPath("data.[].photoViewList[].imageUrl").description("포스트 포토 url")
                        )
                ));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("챌린지 내 본인이 작성한 모든 포스트 조회")
    void findChallengePostsByMe() throws Exception {

        // given
        List<PostViewResponse> mockPostViewResponseList = List.of(PostViewResponse.builder()
                .id(1L)
                .challengeEnrollmentId(1L)
                .content("This is test post for getting challenge posts by me.")
                .writer("test user")
                .isAnnouncement(false)
                .createdAt(LocalDateTime.now())
                .photoViewList(List.of(new PostPhotoView(1L, 1L, "https://picsum.photos/id/40/200/300")))
                .build());

        given(challengePostSearchService.findChallengePostsByMember(anyLong(), any(Member.class), any(Pageable.class)))
                .willReturn(SuccessResponse.of("", mockPostViewResponseList));

        // when
        ResultActions result = mockMvc.perform(get("/api/challenges/{id}/posts/me", 1L)
                .param("page", "0")
                .param("size", "10")
                .param("sort", "")
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN"));

        //then
        result.andExpect(status().isOk())
                .andDo(document("challengePost/get-challenge-posts-by-me",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("message").description("메시지"),
                                fieldWithPath("data.[].id").description("포스트 id"),
                                fieldWithPath("data.[].challengeEnrollmentId").description("포스트가 소속된 enrollment id"),
                                fieldWithPath("data.[].content").description("포스트 내용"),
                                fieldWithPath("data.[].writer").description("작성자"),
                                fieldWithPath("data.[].isAnnouncement").description("공지글 여부"),
                                fieldWithPath("data.[].createdAt").description("생성 일시"),
                                fieldWithPath("data.[].photoViewList").description("포스트 포토(URL 포함) 데이터를 담은 객체 배열"),
                                fieldWithPath("data.[].photoViewList[].postPhotoId").description("포토 id(삭제 예정)"),
                                fieldWithPath("data.[].photoViewList[].viewOrder").description("포스트 내 포토의 순서"),
                                fieldWithPath("data.[].photoViewList[].imageUrl").description("포스트 포토 url")
                        )
                ));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("챌린지 포스트 생성")
    void createPost() throws Exception {

        //given
        AddPostRequest mockAddPostRequest = AddPostRequest.builder()
                .content("I want to create this post.")
                .isAnnouncement(false)
                .photos(List.of(new AddPostPhotoData(1L, "jpg", anyLong())))
                .build();

        List<String> presignedUrlList = List.of("https://please.upload/your-photo/here");

        given(challengePostCreationService.createPost(any(AddPostRequest.class), anyLong(), any(Member.class)))
                .willReturn(SuccessResponse.of("포스트가 생성되었습니다.", presignedUrlList));

        //when
        ResultActions result = mockMvc.perform(post("/api/challenges/{id}/post", 1L)
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
                                fieldWithPath("data").description("AWS S3 업로드를 위한 url List"),
                                fieldWithPath("data[]").description("AWS S3 업로드를 위한 preSignedUrl")
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

        given(challengePostUpdateService.patchPost(any(ModifyPostRequest.class), anyLong(), any(Member.class)))
                .willReturn(SuccessResponse.of("포스트가 수정되었습니다.", presignedUrlList));

        //when
        ResultActions result = mockMvc.perform(patch("/api/posts/{id}", 1L)
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
                                fieldWithPath("modifiedPhotos[].photoId").description("정렬 순서를 변경하려는 이미지 파일의 PostPhotoId"),
                                fieldWithPath("modifiedPhotos[].viewOrder").description("변경하려는 정렬 순서"),
                                fieldWithPath("deletedPhotoIds").description("삭제한 이미지 파일 목록"),
                                fieldWithPath("deletedPhotoIds[]").description("삭제할 이미지 파일의 PostPhotoId")
                                ),
                        responseFields(
                                fieldWithPath("message").description("메시지"),
                                fieldWithPath("data").description("AWS S3 업로드를 위한 url List"),
                                fieldWithPath("data[]").description("AWS S3 업로드를 위한 preSignedUrl")
                        )
                ));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("챌린지 포스트 삭제")
    void deletePost() throws Exception {

        //given
        doNothing().when(challengePostDeleteService).deletePost(anyLong(), any(Member.class));

        //when
        ResultActions result = mockMvc.perform(delete("/api/posts/{id}", 1L)
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_PREFIX + "ACCESS_TOKEN"));

        //then
        result.andExpect(status().isOk())
                .andDo(document("challengePost/delete-challenge-post",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("message").description("메시지"),
                                fieldWithPath("data").description("빈 데이터(null)")
                        )
                ));
    }

}