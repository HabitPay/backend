package com.habitpay.habitpay.domain.challengePost.api;

import com.habitpay.habitpay.domain.challengePost.application.ChallengePostService;
import com.habitpay.habitpay.domain.challengePost.dao.ChallengePostRepository;
import com.habitpay.habitpay.domain.challengePost.domain.ChallengePost;
import com.habitpay.habitpay.domain.challengePost.dto.AddPostRequest;
import com.habitpay.habitpay.domain.challengePost.dto.ModifyPostRequest;
import com.habitpay.habitpay.domain.challengePost.dto.PostPhotoView;
import com.habitpay.habitpay.domain.challengePost.dto.PostViewResponse;
import com.habitpay.habitpay.domain.postPhoto.application.PostPhotoService;
import com.habitpay.habitpay.domain.postPhoto.dao.PostPhotoRepository;
import com.habitpay.habitpay.domain.postPhoto.domain.PostPhoto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChallengePostApi {

    private final ChallengePostService challengePostService;
    private final PostPhotoService postPhotoService;

    @GetMapping("/posts/{id}")
    //    @GetMapping("/api/posts/{id}")
    public ResponseEntity<PostViewResponse> findPost(@PathVariable Long id) {

        ChallengePost challengePost = challengePostService.findById(id);
        List<PostPhoto> photoList = postPhotoService.findAllByPost(id);
        List<PostPhotoView> photoViewList = postPhotoService.makePhotoViewList(photoList);

        return ResponseEntity.ok()
                .body(new PostViewResponse(challengePost, photoViewList));
    }

    // todo : 한 챌린지 내의 모든 포스트 보기
//    @GetMapping("/api/challenge/{id}/posts")
//    public ResponseEntity<List<PostViewResponse>> findChallengePosts(@PathVariable Long id) {
//        List<PostViewResponse> posts = challengePostService.findAllByChallenge(id)
//                .stream()
//                .map(PostViewResponse::new)
//                .toList();
//
//        return ResponseEntity.ok()
//                .body(posts);
//    }

    // todo : 한 챌린지 내에서 내가 등록한 포스트만 모아보기
    @GetMapping("/challenge_enrollment/{id}/posts")
//    @GetMapping("/api/challenge_enrollment/{id}/posts")
    public ResponseEntity<List<PostViewResponse>> findChallengeEnrollmentPosts(@PathVariable Long id) {
        List<PostViewResponse> posts = challengePostService.findAllByChallengeEnrollment(id)
                .stream()
                // .filter() // todo : isAnnouncement == true 인 건 제외하기
                .map(post -> new PostViewResponse(post, postPhotoService.makePhotoViewList(postPhotoService.findAllByPost(post.getId()))))
                .toList();

        return ResponseEntity.ok()
                .body(posts);
    }

    @PostMapping("/challenge_enrollment/{id}/post")
    //@PostMapping("/api/challenge_enrollment/{id}/post")
    public ResponseEntity<List<String>> addPost(@RequestBody AddPostRequest request, @PathVariable Long id, Principal principal) {

        ChallengePost challengePost = challengePostService.save(request, id);

        // todo : principal.getName() 정보 확인 필요 없으면 인자 지우기
        // System.out.println("Principal: " + principal.getName());

        List<String> preSignedUrlList = postPhotoService.save(challengePost, request.getPhotos());

        return ResponseEntity.status(HttpStatus.CREATED).body(preSignedUrlList);
    }

    // todo : PostViewResponse로 응답 본문 형태 바꾸기
    @PutMapping("/api/posts/{id}")
    public ResponseEntity<ChallengePost> modifyPost(@PathVariable Long id,
                                                    @RequestBody ModifyPostRequest request) {
        ChallengePost modifiedPost = challengePostService.update(id, request);

        return ResponseEntity.ok()
                .body(modifiedPost);
    }

    // todo : 공지글만 지워지는 것 확인하기
    @DeleteMapping("/api/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        challengePostService.delete(id);

        return ResponseEntity.ok()
                .build();
    }
}
