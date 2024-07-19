package com.habitpay.habitpay.domain.challengepost.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.postphoto.dto.PostPhotoView;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
public class PostViewResponse {
    // todo : id, challengeEnrollmentId 필요 없으면 지우기
    private Long id;
    private Long challengeEnrollmentId;
    private String content;
    private String writer;
    private Boolean isAnnouncement;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
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
