package com.habitpay.habitpay.domain.challengepost.api;

import com.habitpay.habitpay.domain.challengepost.application.*;
import com.habitpay.habitpay.domain.challengepost.dto.AddPostRequest;
import com.habitpay.habitpay.domain.challengepost.dto.ModifyPostRequest;
import com.habitpay.habitpay.domain.challengepost.dto.PostViewResponse;
import com.habitpay.habitpay.global.config.auth.CustomUserDetails;
import com.habitpay.habitpay.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChallengePostApi {

    private final ChallengePostCreationService challengePostCreationService;
    private final ChallengePostSearchService challengePostSearchService;
    private final ChallengePostUpdateService challengePostUpdateService;
    private final ChallengePostDeleteService challengePostDeleteService;
    private final ChallengePostUtilService challengePostUtilService;

    private static final String DEFAULT_PAGE = "0";
    private static final String DEFAULT_SIZE = "5";
    private static final String DEFAULT_SORT = "asc";

    @GetMapping("/api/posts/{id}")
    public SuccessResponse<PostViewResponse> getPost(@PathVariable Long id) {

        return challengePostSearchService.getPostViewResponseByPostId(id);
    }

    @GetMapping("/api/challenges/{id}/posts")
    public SuccessResponse<List<PostViewResponse>> getChallengePosts(
            @PathVariable Long id,
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_SIZE) int size,
            @RequestParam(defaultValue = DEFAULT_SORT) String[] sort) {

        Pageable pageable = challengePostUtilService.makePageable(page, size, sort);

        return challengePostSearchService.findPostViewResponseListByChallengeId(id, pageable);
    }

    @GetMapping("/api/challenges/{id}/posts/me")
    public SuccessResponse<List<PostViewResponse>> getChallengePostsByMe(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_SIZE) int size,
            @RequestParam(defaultValue = DEFAULT_SORT) String[] sort) {

        Pageable pageable = challengePostUtilService.makePageable(page, size, sort);

        return challengePostSearchService.findChallengePostsByMember(id, user.getMember(), pageable);
    }

    // -----------------------------------------------------------------------------
    // todo : 'challengeEnrollmentId' or 'memberId' 등 멤버 식별할 수 있는 데이터를 받아야 함
//    @GetMapping("/api/challenges/{id}/posts/member")
//    public SuccessResponse<List<PostViewResponse>> getChallengePostsByMember(
//            @PathVariable Long id,
//            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
//            @RequestParam(defaultValue = DEFAULT_SIZE) int size,
//            @RequestParam(defaultValue = DEFAULT_SORT) String[] sort) {
//
//        String memberEmail = "otherMember@email.address"; // todo : 임시
//        Pageable pageable = challengePostUtilService.makePageable(page, size, sort);
//
//        // todo: member 객체를 깔끔하게 받을 수 없으면, 추가로 메서드 만들자
//        return challengePostSearchService.findChallengePostsByMember(id, memberEmail, pageable);
//    }
    // -------------------------------------------------------------------------------

    // String 감싸는 응답 DTO 만들어서 적용하기 (아래 메서드도 같이!)
    @PostMapping("/api/challenges/{id}/post")
    public SuccessResponse<List<String>> createPost(
            @RequestBody AddPostRequest request,
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user) {

        return challengePostCreationService.createPost(request, id, user.getMember());
    }

    @PatchMapping("/api/posts/{id}")
    public SuccessResponse<List<String>> patchPost(
            @RequestBody ModifyPostRequest request, @PathVariable Long id, @AuthenticationPrincipal CustomUserDetails user) {

        return challengePostUpdateService.patchPost(request, id, user.getMember());
    }

    @DeleteMapping("/api/posts/{id}")
    public SuccessResponse<Void> deletePost(
            @PathVariable Long id, @AuthenticationPrincipal CustomUserDetails user) {

        challengePostDeleteService.deletePost(id, user.getMember());
        return SuccessResponse.of(
                "포스트가 정상적으로 삭제되었습니다.",
                null
        );
    }
}
