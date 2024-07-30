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
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping("/api/posts/{id}")
    public SuccessResponse<PostViewResponse> getPost(@PathVariable Long id) {

        return challengePostSearchService.getPostViewByPostId(id);
    }

    @GetMapping("/api/challenges/{id}/posts")
    public SuccessResponse<Slice<PostViewResponse>> getChallengePosts(
            @PathVariable Long id,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        return challengePostSearchService.findPostViewListByChallengeId(id, pageable);
    }

    @GetMapping("/api/challenges/{id}/posts/announcements")
    public SuccessResponse<Slice<PostViewResponse>> getAnnouncements(
            @PathVariable Long id,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        return challengePostSearchService.findAnnouncementPostViewListByChallengeId(id, pageable);
    }

    @GetMapping("/api/challenges/{id}/posts/me")
    public SuccessResponse<Slice<PostViewResponse>> getChallengePostsByMe(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        return challengePostSearchService.findPostViewListByMember(id, user.getMember(), pageable);
    }

    // -----------------------------------------------------------------------------
    // todo : 'challengeEnrollmentId' or 'memberId' 등 멤버 식별할 수 있는 데이터를 받아야 함
//    @GetMapping("/api/challenges/{id}/posts/member")
//    public SuccessResponse<Slice<PostViewResponse>> getChallengePostsByMember(
//            @PathVariable Long id,
//            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
//
//        String memberEmail = "otherMember@email.address"; // todo : 임시
//
//        return challengePostSearchService.findChallengePostsByMember(id, memberEmail, pageable);
//    }
    // -------------------------------------------------------------------------------

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

        return challengePostDeleteService.deletePost(id, user.getMember());
    }
}
