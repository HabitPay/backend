package com.habitpay.habitpay.domain.challengePost.api;

import com.habitpay.habitpay.domain.challengePost.application.ChallengePostService;
import com.habitpay.habitpay.domain.challengePost.dao.ChallengePostRepository;
import com.habitpay.habitpay.domain.challengePost.domain.ChallengePost;
import com.habitpay.habitpay.domain.challengePost.dto.AddPostRequest;
import com.habitpay.habitpay.domain.challengePost.dto.ModifyPostRequest;
import com.habitpay.habitpay.domain.challengePost.dto.PostViewResponse;
import com.habitpay.habitpay.domain.postPhoto.application.PostPhotoService;
import com.habitpay.habitpay.domain.postPhoto.dao.PostPhotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChallengePostApi {

    private final ChallengePostRepository challengePostRepository;
    private final ChallengePostService challengePostService;
    private final PostPhotoRepository postPhotoRepository;
    private final PostPhotoService postPhotoService;

    @GetMapping("/api/posts/{id}")
    public ResponseEntity<PostViewResponse> findPost(@PathVariable Long id) {
        ChallengePost challengePost = challengePostService.findById(id);

        return ResponseEntity.ok()
                .body(new PostViewResponse(challengePost));
    }
    // todo : test 용도 (auth 필요 없는 컨트롤러)
//    @GetMapping("/posts/{id}")
//    public ResponseEntity<String> findPost(@PathVariable("id")String id) {
//        return ResponseEntity.ok()
//                .body("Post id : " + id);
//    }
//    @GetMapping("/posts")
//    public ResponseEntity<PostViewResponse> findPost(@RequestParam(value = "id")Long id) {
//        ChallengePost challengePost = challengePostService.findById(id);
//
//        return ResponseEntity.ok()
//                .body(new PostViewResponse(challengePost));
//    }

    @GetMapping("/api/challenge/{id}/posts")
    public ResponseEntity<List<PostViewResponse>> findChallengePosts(@PathVariable Long id) {
        List<PostViewResponse> posts = challengePostService.findAllByChallenge(id)
                .stream()
                .map(PostViewResponse::new)
                .toList();

        return ResponseEntity.ok()
                .body(posts);
    }

    @GetMapping("/api/challenge_enrollment/{id}/posts")
    public ResponseEntity<List<PostViewResponse>> findChallengeEnrollmentPosts(@PathVariable Long id) {
        List<PostViewResponse> posts = challengePostService.findAllByChallengeEnrollment(id)
                .stream()
                .map(PostViewResponse::new)
                .toList();

        return ResponseEntity.ok()
                .body(posts);
    }

//    @PostMapping("/api/challenge_enrollment/{id}/post")
//    public ResponseEntity<ChallengePost> addPost(@RequestBody AddPostRequest request, @PathVariable Long id, Principal principal) {
//        ChallengePost challengePost = challengePostService.save(request, id);
//        // todo : principal.getName() 정보 확인 필요 없으면 인자 지우기
//        System.out.println("Principal: " + principal.getName());
//
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(challengePost);
//    }
    @PostMapping("/post")
    public ResponseEntity<ChallengePost> addPost(@RequestBody AddPostRequest request, Principal principal) {
        // todo : enrollmentId 만들고 난 후 수정하기
        ChallengePost challengePost = challengePostService.save(request, 1L);

        // todo : principal.getName() 정보 확인 필요 없으면 인자 지우기
//        System.out.println("Principal: " + principal.getName());

        List<String> preSignedUrlList = postPhotoService.save(request.getPhotos());

        // todo : postPhoto 저장할 url 링크 보내줘야 함 -> front에서 어떻게 처리되는지?
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(challengePost);
//        return ResponseEntity.status(HttpStatus.CREATED).body(preSignedUrlList);
    }

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
