package com.habitpay.habitpay.domain.challengepost.api;

import com.habitpay.habitpay.domain.challenge.dao.ChallengeRepository;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.dao.ChallengeEnrollmentRepository;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengepost.application.ChallengePostService;
import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.challengepost.dto.AddPostRequest;
import com.habitpay.habitpay.domain.challengepost.dto.ModifyPostRequest;
import com.habitpay.habitpay.domain.member.dao.MemberRepository;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.postphoto.dto.PostPhotoView;
import com.habitpay.habitpay.domain.challengepost.dto.PostViewResponse;
import com.habitpay.habitpay.domain.postphoto.application.PostPhotoService;
import com.habitpay.habitpay.domain.postphoto.domain.PostPhoto;
import com.habitpay.habitpay.domain.refreshtoken.exception.CustomJwtException;
import com.habitpay.habitpay.global.error.CustomJwtErrorInfo;
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

    private final ChallengePostService challengePostService;
    private final PostPhotoService postPhotoService;

    // todo : 나중에 각 서비스에 찾는 메서드 추가로 만들고, 서비스를 불러오는 게 좋을 듯
    //      : 지금은 돌아가는 거 확인하는 목표로 우선 repo로 하기
    private final ChallengeRepository challengeRepository;
    private final MemberRepository memberRepository;
    private final ChallengeEnrollmentRepository challengeEnrollmentRepository;


    @GetMapping("/api/posts/{id}")
    public ResponseEntity<PostViewResponse> findPost(@PathVariable Long id) {

        ChallengePost challengePost = challengePostService.findById(id);
        List<PostPhoto> photoList = postPhotoService.findAllByPost(challengePost);
        List<PostPhotoView> photoViewList = postPhotoService.makePhotoViewList(photoList);

        return ResponseEntity.ok()
                .body(new PostViewResponse(challengePost, photoViewList));
    }

    @GetMapping("/api/challenges/{id}/posts")
    public ResponseEntity<List<PostViewResponse>> findChallengePosts(@PathVariable Long id) {
        List<PostViewResponse> challengePostsView = challengePostService.findAllByChallenge(id)
                .stream()
                .filter(post -> !post.getIsAnnouncement())
                // .sorted() // todo : 순서 설정하고 싶을 때
                .map(post -> new PostViewResponse(post, postPhotoService.makePhotoViewList(postPhotoService.findAllByPost(post))))
                .toList();

        return ResponseEntity.ok()
                .body(challengePostsView);
    }

    @GetMapping("/api/challenges/{id}/posts/me")
    public ResponseEntity<List<PostViewResponse>> findChallengePostsByMe(
            @PathVariable Long id, Principal principal) {
        // todo : principal.getName() : token의 email 주소
        //      : [email && challenge id] 정보를 합쳐 enrollment id 찾기
        Long challengeEnrollmentId = 1L; // todo : 임시값

        List<PostViewResponse> challengePostsView = challengePostService.findAllByChallengeEnrollment(challengeEnrollmentId)
                .stream()
                .filter(post -> !post.getIsAnnouncement())
                // .sorted() // todo : 순서 설정하고 싶을 때
                .map(post -> new PostViewResponse(post, postPhotoService.makePhotoViewList(postPhotoService.findAllByPost(post))))
                .toList();

        return ResponseEntity.ok()
                .body(challengePostsView);
    }

    // todo : 경로 및 필요한 requestParam 설정 완료 후 다시 고치기
    //      : 'challengeEnrollmentId' or 'memberId' 등 멤버 식별할 수 있는 데이터를 받으면 됨
//    @GetMapping("/api/challenges/{id}/posts/member")
//    public ResponseEntity<List<PostViewResponse>> findChallengePostsByMember(
//            @PathVariable Long id, @RequestParam Optional<Long> challengeEnrollmentId) {
//
//        Long challengeEnrollmentId = 1L;
//
//        List<PostViewResponse> challengePostsView = challengePostService.findAllByChallengeEnrollment(challengeEnrollmentId)
//                .stream()
//                .filter(post -> !post.getIsAnnouncement())
//                // .sorted() // todo : 순서 설정하고 싶을 때
//                .map(post -> new PostViewResponse(post, postPhotoService.makePhotoViewList(postPhotoService.findAllByPost(post))))
//                .toList();
//
//        return ResponseEntity.ok()
//                .body(challengePostsView);
//    }

    @PostMapping("/api/challenges/{id}/post")
    public ResponseEntity<List<String>> addPost(@RequestBody AddPostRequest request, @PathVariable Long id, @AuthenticationPrincipal String email) {

         log.info("Authentication Principal Email: {}", email);

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("(for debugging) no such member : " + email));
        // todo : List<>로 받게 될 경우 'challenge id' 이용해 enrollment 특정해야 함
        ChallengeEnrollment enrollment = challengeEnrollmentRepository.findByMember(member)
                .orElseThrow(() -> new NoSuchElementException("(for debugging) no enrollment for : " + email));

        if (request.getIsAnnouncement()) {
            Challenge challenge = challengeRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("(for debugging) no such challenge : " + id));
            if (!challenge.getHost().equals(member)) {
                throw new CustomJwtException(HttpStatus.FORBIDDEN, CustomJwtErrorInfo.FORBIDDEN, "Only Host is able to upload an Announcement Post.");
            }
        }

        ChallengePost challengePost = challengePostService.save(request, enrollment.getId());
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
