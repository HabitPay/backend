package com.habitpay.habitpay.domain.challengepost.application;

import com.habitpay.habitpay.domain.challenge.application.ChallengeSearchService;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challengeenrollment.application.ChallengeEnrollmentSearchService;
import com.habitpay.habitpay.domain.challengeenrollment.dao.ChallengeEnrollmentRepository;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengepost.dao.ChallengePostRepository;
import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.challengepost.dto.PostViewResponse;
import com.habitpay.habitpay.domain.member.application.MemberService;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.postphoto.application.PostPhotoSearchService;
import com.habitpay.habitpay.domain.postphoto.application.PostPhotoUtilService;
import com.habitpay.habitpay.domain.postphoto.domain.PostPhoto;
import com.habitpay.habitpay.domain.postphoto.dto.PostPhotoView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengePostSearchService {

    private final PostPhotoSearchService postPhotoSearchService;
    private final PostPhotoUtilService postPhotoUtilService;
    private final MemberService memberService;
    private final ChallengeEnrollmentSearchService challengeEnrollmentSearchService;
    private final ChallengeSearchService challengeSearchService;

    private final ChallengePostRepository challengePostRepository;
    private final ChallengeEnrollmentRepository challengeEnrollmentRepository;

    public PostViewResponse getPostViewResponseByPostId(Long postId) {
        ChallengePost challengePost = this.getChallengePostById(postId);
        List<PostPhoto> photoList = postPhotoSearchService.findAllByPost(challengePost);
        List<PostPhotoView> photoViewList = postPhotoUtilService.makePhotoViewList(photoList);

        return new PostViewResponse(challengePost, photoViewList);
    }

    public List<PostViewResponse> findPostViewResponseListByChallengeId(Long challengeId, Pageable pageable) {

        return this.findAllByChallengeId(challengeId, pageable)
                .stream()
                // .filter(post -> !post.getIsAnnouncement()) // todo
                // .sorted() // todo : 순서 설정하고 싶을 때
                .map(post -> {
                    List<PostPhotoView> photoViewList = postPhotoUtilService.makePhotoViewList(postPhotoSearchService.findAllByPost(post));
                    return new PostViewResponse(post, photoViewList);
                })
                .toList();
    }

    public List<PostViewResponse> findChallengePostsByMember(Long challengeId, String email, Pageable pageable) {
        Member member = memberService.findByEmail(email);
        Challenge challenge = challengeSearchService.getChallengeById(challengeId);
        ChallengeEnrollment enrollment = challengeEnrollmentSearchService.findByMemberAndChallenge(member, challenge)
                .orElseThrow(() -> new NoSuchElementException("챌린지에 등록된 멤버가 아닙니다."));

        Long challengeEnrollmentId = enrollment.getId();

        return challengePostRepository.findAllByChallengeEnrollmentId(challengeEnrollmentId, pageable)
                .stream()
                // .filter(post -> !post.getIsAnnouncement()) // todo
                // .sorted() // todo : 순서 설정하고 싶을 때
                .map(post -> {
                    List<PostPhotoView> photoViewList = postPhotoUtilService.makePhotoViewList(postPhotoSearchService.findAllByPost(post));
                    return new PostViewResponse(post, photoViewList);
                })
                .toList();
    }

    public ChallengePost getChallengePostById(Long id) {
        return challengePostRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("포스트를 찾을 수 없습니다."));
    }

    // todo : 각 챌린지 별로 findAll 해주는 메서드
    public List<ChallengePost> findAllByChallengeId(Long challengeId, Pageable pageable) {
        return challengePostRepository.findAllByChallengeEnrollmentId(1L, pageable); // todo : 임시값

        // 방법 1 :
        // 챌린지 아이디를 이용해 챌린지를 찾는다 (DB 검색1)
        // 챌린지 등록 레포에서 해당 챌린지에 맞는 등록 객체 리스트를 얻는다 (DB 검색2)
        // 각 등록 객체를 기반으로 포스트 목록을 불러온다 (DB 검색3)
        // 이걸 또 시간이나 id 순서로 재정렬,,

        // 방법 2 :
        // 포스트 도메인에 챌린지도 외래키로 연결한다
        // 챌린지 아이디로 모든 포스트를 찾는다! (DB 검색1)

        // 방법 3 :
        // DB View 사용하기 (잘 몰라서 학습 필요)

        // 방법 1로 하려고 처음에 DB를 구성했지만, DB 검색을 3번이나 거치는 비용 + 멤버 별 포스트 목록을 각각 불러온 다음 다시 순서대로 리스트업 하는 비용이 클 듯함
        // 특히 포스트는 아마 전체 도메인 중 가장 많은 DB 객체가 생성되는 도메인이 될 것임
        // 조회도 자주 발생할 것이라, 많은 양을 자주 조회하는 DB가 될 것,,

        // 조회 성능과 데이터 정합성 중에 선택의 기로

        // 각각 만들어보고 처리 시간 비교해보고 이것저것 고민해야 할 듯 하다
    }

    public Challenge getChallengeByPostId(Long postId) {
        ChallengePost post = getChallengePostById(postId);
        // todo : enrollment service에 findById() 메서드 만들기
        ChallengeEnrollment enrollment = challengeEnrollmentRepository
                .findById(post.getChallengeEnrollment().getId())
                .orElseThrow(() -> new NoSuchElementException("포스트 소속 정보를 찾을 수 없습니다."));
        return enrollment.getChallenge();
    }

}
