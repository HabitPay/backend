package com.habitpay.habitpay.domain.challengepost.api;

import com.habitpay.habitpay.domain.challengepost.application.*;
import com.habitpay.habitpay.domain.challengepost.dto.AddPostRequest;
import com.habitpay.habitpay.domain.challengepost.dto.ModifyPostRequest;
import com.habitpay.habitpay.domain.challengepost.dto.PostViewResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<PostViewResponse> findPost(@PathVariable Long id) {

        return ResponseEntity.ok()
                .body(challengePostSearchService.findPostById(id));
    }

    @GetMapping("/api/challenges/{id}/posts")
    public ResponseEntity<List<PostViewResponse>> findChallengePosts(@PathVariable Long id) {

        return ResponseEntity.ok()
                .body(challengePostSearchService.findChallengePostsByChallengeId(id));
    }

    @GetMapping("/api/challenges/{id}/posts/me")
    public ResponseEntity<List<PostViewResponse>> findChallengePostsByMe(
            @PathVariable Long id, @AuthenticationPrincipal String email) {

        return ResponseEntity.ok()
                .body(challengePostSearchService.findChallengePostsByMember(id, email));
    }

    // -----------------------------------------------------------------------------
    // todo : 'challengeEnrollmentId' or 'memberId' 등 멤버 식별할 수 있는 데이터를 받아야 함
    @GetMapping("/api/challenges/{id}/posts/member")
    public ResponseEntity<List<PostViewResponse>> findChallengePostsByMember(
            @PathVariable Long id) {

        String memberEmail = "otherMember's@email.address"; // todo : 임시

        return ResponseEntity.ok()
                .body(challengePostSearchService.findChallengePostsByMember(id, memberEmail));
    }
    // -------------------------------------------------------------------------------

    @PostMapping("/api/challenges/{id}/post")
    public ResponseEntity<List<String>> addPost(
            @RequestBody AddPostRequest request,
            @PathVariable Long id,
            @AuthenticationPrincipal String email) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(challengePostCreationService.save(request, id, email));
    }

    @PutMapping("/api/posts/{id}")
    public ResponseEntity<List<String>> modifyPost(
            @RequestBody ModifyPostRequest request, @PathVariable Long id) {

        return ResponseEntity.ok()
                .body(challengePostUpdateService.update(request, id));
    }

    @DeleteMapping("/api/posts/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id, @AuthenticationPrincipal String email) {

        challengePostDeleteService.delete(id, email);
        return ResponseEntity.ok()
                .build();
    }
}
