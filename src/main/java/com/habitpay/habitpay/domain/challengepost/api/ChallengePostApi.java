package com.habitpay.habitpay.domain.challengepost.api;

import com.habitpay.habitpay.domain.challenge.application.ChallengeSearchService;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.application.ChallengeEnrollmentSearchService;
import com.habitpay.habitpay.domain.challengeenrollment.dao.ChallengeEnrollmentRepository;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengepost.application.*;
import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.challengepost.dto.AddPostRequest;
import com.habitpay.habitpay.domain.challengepost.dto.ModifyPostRequest;
import com.habitpay.habitpay.domain.member.application.MemberService;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.postphoto.application.PostPhotoCreationService;
import com.habitpay.habitpay.domain.postphoto.application.PostPhotoDeleteService;
import com.habitpay.habitpay.domain.postphoto.application.PostPhotoSearchService;
import com.habitpay.habitpay.domain.postphoto.application.PostPhotoUtilService;
import com.habitpay.habitpay.domain.postphoto.dto.PostPhotoView;
import com.habitpay.habitpay.domain.challengepost.dto.PostViewResponse;
import com.habitpay.habitpay.domain.postphoto.domain.PostPhoto;
import com.habitpay.habitpay.domain.refreshtoken.exception.CustomJwtException;
import com.habitpay.habitpay.global.error.CustomJwtErrorInfo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;

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
                .body(challengePostSearchService.findPost(id));
    }

    @GetMapping("/api/challenges/{id}/posts")
    public ResponseEntity<List<PostViewResponse>> findChallengePosts(@PathVariable Long id) {

        return ResponseEntity.ok()
                .body(challengePostSearchService.findChallengePosts(id));
    }

    @GetMapping("/api/challenges/{id}/posts/me")
    public ResponseEntity<List<PostViewResponse>> findChallengePostsByMe(
            @PathVariable Long id, @AuthenticationPrincipal String email) {

        return ResponseEntity.ok()
                .body(challengePostSearchService.findChallengePostsByMe(id, email));
    }

    // -----------------------------------------------------------------------------
    // todo : 'challengeEnrollmentId' or 'memberId' 등 멤버 식별할 수 있는 데이터를 받아야 함
    @GetMapping("/api/challenges/{id}/posts/member")
    public ResponseEntity<List<PostViewResponse>> findChallengePostsByMember(
            @PathVariable Long id, @AuthenticationPrincipal String email) {

        return ResponseEntity.ok()
                .body(challengePostSearchService.findChallengePostsByMember(id, email));
    }
    // -------------------------------------------------------------------------------

    @PostMapping("/api/challenges/{id}/post")
    public ResponseEntity<List<String>> addPost(
            @RequestBody AddPostRequest request,
            @PathVariable Long id,
            @AuthenticationPrincipal String email) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(challengePostCreationService.addPost(request, id, email));
    }

    @PutMapping("/api/posts/{id}")
    public ResponseEntity<List<String>> modifyPost(
            @RequestBody ModifyPostRequest request, @PathVariable Long id) {

        return ResponseEntity.ok()
                .body(challengePostUpdateService.modifyPost(request, id));
    }

    @DeleteMapping("/api/posts/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id, @AuthenticationPrincipal String email) {

        challengePostDeleteService.deletePost(id, email);
        return ResponseEntity.ok()
                .build();
    }
}
