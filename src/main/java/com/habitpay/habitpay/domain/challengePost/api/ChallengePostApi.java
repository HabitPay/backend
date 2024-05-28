package com.habitpay.habitpay.domain.challengePost.api;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengePost.application.ChallengePostService;
import com.habitpay.habitpay.domain.challengePost.domain.ChallengePost;
import com.habitpay.habitpay.domain.challengePost.dto.AddPostRequest;
import com.habitpay.habitpay.domain.challengePost.dto.ModifyPostRequest;
import com.habitpay.habitpay.domain.postPhoto.dto.ModifyPostPhotoData;
import com.habitpay.habitpay.domain.postPhoto.dto.PostPhotoView;
import com.habitpay.habitpay.domain.challengePost.dto.PostViewResponse;
import com.habitpay.habitpay.domain.postPhoto.application.PostPhotoService;
import com.habitpay.habitpay.domain.postPhoto.domain.PostPhoto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChallengePostApi {

    private final ChallengePostService challengePostService;
    private final PostPhotoService postPhotoService;


    @GetMapping("/api/posts/{id}")
    public ResponseEntity<PostViewResponse> findPost(@PathVariable Long id) {

        ChallengePost challengePost = challengePostService.findById(id);
        List<PostPhoto> photoList = postPhotoService.findAllByPost(challengePost);
        List<PostPhotoView> photoViewList = postPhotoService.makePhotoViewList(photoList);

        return ResponseEntity.ok()
                .body(new PostViewResponse(challengePost, photoViewList));
    }

    @GetMapping("/api/challenges/{id}/posts")
    public ResponseEntity<List<PostViewResponse>> findChallengePosts(
            @PathVariable Long id, @RequestParam(required = false) Optional<Long> challengeEnrollmentId) {

        Long enrollmentId = challengeEnrollmentId.orElse(-1L);
        List<ChallengePost> challengePosts = new ArrayList<>();
        List<PostViewResponse> viewPosts = new ArrayList<>();

        if (enrollmentId.equals(-1L)) {
             challengePosts = challengePostService.findAllByChallenge(id);
        } else {
            challengePosts = challengePostService.findAllByChallengeEnrollment(enrollmentId);
        }

        viewPosts = challengePosts
                .stream()
                .filter(post -> !post.getIsAnnouncement())
                // .sorted() // todo : id 순이 아닌 다른 순서로 정렬하고 싶을 경우
                .map(post -> new PostViewResponse(post, postPhotoService.makePhotoViewList(postPhotoService.findAllByPost(post))))
                .toList();

        return ResponseEntity.ok()
                .body(viewPosts);
    }

    @PostMapping("/api/challenge_enrollment/{id}/post")
    public ResponseEntity<List<String>> addPost(@RequestBody AddPostRequest request, @PathVariable Long id, Principal principal) {

         log.info("Principal Name: {}", principal.getName());
        // todo : principal.getName() : token의 email 주소
        //      : API를 '/api/challenges/{id}/post'로 변경하여, 메서드 간 일관성을 유지하고 직관성을 높일 예정
        //      : [email && challenge id] 정보를 합쳐 enrollment id 찾기만 하면 됨

        ChallengePost challengePost = challengePostService.save(request, id);
        List<String> preSignedUrlList = postPhotoService.save(challengePost, request.getPhotos());

        return ResponseEntity.status(HttpStatus.CREATED).body(preSignedUrlList);
    }

    @PutMapping("/api/posts/{id}")
    public ResponseEntity<List<String>> modifyPost(@PathVariable Long id,
                                                    @RequestBody ModifyPostRequest request) {

        ChallengePost modifiedPost = challengePostService.update(id, request);
        request.getDeletedPhotoIds().forEach(postPhotoService::delete);
        request.getModifiedPhotos().forEach(photo -> postPhotoService.changeViewOrder(photo.getPhotoId(), photo.getViewOrder()));
        List<String> presignedUrlList =  postPhotoService.save(challengePostService.findById(id), request.getNewPhotos());

        return ResponseEntity.ok()
                .body(presignedUrlList);
    }

    // todo : 공지글만 지워지는 것 확인하기
    @DeleteMapping("/api/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        challengePostService.delete(id);

        return ResponseEntity.ok()
                .build();
    }
}
