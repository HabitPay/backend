package com.habitpay.habitpay.domain.challengepost.api;

import com.habitpay.habitpay.domain.challengepost.application.*;
import com.habitpay.habitpay.domain.challengepost.dto.AddPostRequest;
import com.habitpay.habitpay.domain.challengepost.dto.ModifyPostRequest;
import com.habitpay.habitpay.domain.challengepost.dto.PostViewResponse;
import com.habitpay.habitpay.global.config.auth.CustomUserDetails;
import com.habitpay.habitpay.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

        return SuccessResponse.of(
                "",
                challengePostSearchService.getPostViewResponseByPostId(id)
        );
    }

    @GetMapping("/api/challenges/{id}/posts")
    public SuccessResponse<List<PostViewResponse>> getChallengePosts(@PathVariable Long id) {

        return SuccessResponse.of(
                "",
                challengePostSearchService.findPostViewResponseListByChallengeId(id)
        );
    }

    @GetMapping("/api/challenges/{id}/posts/me")
    public SuccessResponse<List<PostViewResponse>> getChallengePostsByMe(
            @PathVariable Long id, @AuthenticationPrincipal CustomUserDetails user) {

        return SuccessResponse.of(
          "",
          challengePostSearchService.findChallengePostsByMember(id, user.getEmail())
        );
    }

    // -----------------------------------------------------------------------------
    // todo : 'challengeEnrollmentId' or 'memberId' 등 멤버 식별할 수 있는 데이터를 받아야 함
    @GetMapping("/api/challenges/{id}/posts/member")
    public SuccessResponse<List<PostViewResponse>> getChallengePostsByMember(
            @PathVariable Long id) {

        String memberEmail = "otherMember@email.address"; // todo : 임시

        return SuccessResponse.of(
                "",
                challengePostSearchService.findChallengePostsByMember(id, memberEmail)
        );
    }
    // -------------------------------------------------------------------------------

    @PostMapping("/api/challenges/{id}/post")
    public SuccessResponse<List<String>> createPost(
            @RequestBody AddPostRequest request,
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user) {

        return SuccessResponse.of(
                "포스트가 생성되었습니다.",
                challengePostCreationService.createPost(request, id, user.getEmail())
        );
    }

    @PatchMapping("/api/posts/{id}")
    public SuccessResponse<List<String>> patchPost(
            @RequestBody ModifyPostRequest request, @PathVariable Long id, @AuthenticationPrincipal CustomUserDetails user) {

        return SuccessResponse.of(
                "포스트가 수정되었습니다.",
                challengePostUpdateService.patchPost(request, id, user.getEmail())
        );
    }

    @DeleteMapping("/api/posts/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id, @AuthenticationPrincipal String email) {

        challengePostDeleteService.delete(id, email);
        return ResponseEntity.ok()
                .build();
    }
}
