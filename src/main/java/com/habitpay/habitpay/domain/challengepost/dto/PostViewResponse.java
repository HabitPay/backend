package com.habitpay.habitpay.domain.challengepost.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.postphoto.dto.PostPhotoView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class PostViewResponse {
    private Long id;
    private Long challengeEnrollmentId;
    private String content;
    private String writer;
    private Boolean isAnnouncement;
    private LocalDateTime createdAt;
    private List<PostPhotoView> photoViewList;

    public PostViewResponse(ChallengePost post, List<PostPhotoView> photoViewList) {
        this.id = post.getId();
        this.challengeEnrollmentId = post.getChallengeEnrollment().getId();
        this.content = post.getContent();
        this.writer = post.getWriter().getNickname();
        this.isAnnouncement = post.getIsAnnouncement();
        this.createdAt = post.getCreatedAt();
        this.photoViewList = photoViewList;
    }
}
