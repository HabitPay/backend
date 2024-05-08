package com.habitpay.habitpay.domain.challengePost.dto;

import com.habitpay.habitpay.domain.challengePost.domain.ChallengePost;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class PostViewResponse {
    private Long id;
    private Long challengeEnrollmentId;
    private String content;
    private String writer;
    private boolean isAnnouncement;
    private LocalDateTime createdAt;

    public PostViewResponse(ChallengePost post) {
        this.id = post.getId();
        this.challengeEnrollmentId = post.getChallengeEnrollmentId();
        this.content = post.getContent();
        //todo: this.writer = post.getWriter(post.getChallengeEnrollmentId());
        this.isAnnouncement = post.isAnnouncement();
        this.createdAt = post.getCreatedAt();
    }
}
