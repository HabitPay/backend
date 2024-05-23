package com.habitpay.habitpay.domain.challengePost.dto;

import com.habitpay.habitpay.domain.challengePost.domain.ChallengePost;
import com.habitpay.habitpay.domain.postPhoto.domain.PostPhoto;
import com.habitpay.habitpay.domain.postPhoto.dto.PostPhotoData;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
public class PostViewResponse {
    // todo : id, challengeEnrollmentId 필요 없으면 지우기
    private Long id;
    private Long challengeEnrollmentId;
    private String content;
    private String writer;
    private boolean isAnnouncement;
    private LocalDateTime createdAt;
    private List<PostPhotoView> photoViewList;

    public PostViewResponse(ChallengePost post, List<PostPhotoView> photoViewList) {
        this.id = post.getId();
        this.challengeEnrollmentId = post.getChallengeEnrollmentId();
        this.content = post.getContent();
        //todo: this.writer = post.getWriter(post.getChallengeEnrollmentId());
        this.isAnnouncement = post.isAnnouncement();
        this.createdAt = post.getCreatedAt();
        this.photoViewList = photoViewList;
    }
}
